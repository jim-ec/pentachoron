package io.jim.tesserapp.rendering

import android.opengl.GLES10.GL_STACK_OVERFLOW
import android.opengl.GLES10.GL_STACK_UNDERFLOW
import android.opengl.GLES20.*
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.util.RandomAccessBuffer

/**
 * A shader pipeline with a vertex shader, fragment shader an locations of all attributes and
 * uniforms.
 */
class Shader(maxModels: Int) {

    /**
     * Thrown upon OpenGL errors.
     */
    inner class GlException(msg: String) :
            RuntimeException("OpenGL Error 0x${glGetError()} ($errorString): $msg")

    /**
     * Return a string describing the current OpenGL error.
     */
    private val errorString: String
        get() = when (glGetError()) {
            GL_NO_ERROR -> "no error"
            GL_INVALID_ENUM -> "invalid enumeration"
            GL_INVALID_VALUE -> "invalid value"
            GL_INVALID_OPERATION -> "invalid operation"
            GL_INVALID_FRAMEBUFFER_OPERATION -> "invalid framebuffer operation"
            GL_OUT_OF_MEMORY -> "out of memory"
            GL_STACK_UNDERFLOW -> "stack underflow"
            GL_STACK_OVERFLOW -> "stack overflow"
            else -> "unknown error"
        }

    /**
     * GLSL location of position attribute.
     */
    val positionAttributeLocation: Int

    /**
     * GLSL location of color attribute.
     */
    val colorAttributeLocation: Int

    /**
     * GLSL location of model matrix index attribute.
     * Although it's an index into the model matrix array, it is represented as a float.
     */
    val modelIndexAttributeLocation: Int

    private val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    private val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
    private val program = glCreateProgram()
    private val modelMatrixLocation: Int
    private val viewMatrixLocation: Int
    private val projectionMatrixLocation: Int

    private val vertexShaderSource = """
            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M[$maxModels];

            attribute vec3 position;
            attribute vec3 color;
            attribute float modelIndex;

            varying vec3 vColor;

            void main() {
                gl_Position = P * V * M[int(modelIndex)] * vec4(position, 1.0);
                vColor = color;
            }
        """

    private val fragmentShaderSource = """
            varying mediump vec3 vColor;

            void main() {
                gl_FragColor = vec4(vColor, 1.0);
            }
        """

    init {
        if (vertexShader < 0) throw GlException("Cannot create vertex shader")
        if (fragmentShader < 0) throw GlException("Cannot create fragment shader")
        if (program < 0) throw GlException("Cannot create shader program")

        val status = IntArray(1)

        vertexShader.also {
            glShaderSource(it, vertexShaderSource)
            glCompileShader(it)
            glGetShaderiv(it, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw GlException("Cannot compile vertex shader: ${glGetShaderInfoLog(it)}")
            }
        }

        fragmentShader.also {
            glShaderSource(it, fragmentShaderSource)
            glCompileShader(it)
            glGetShaderiv(it, GL_COMPILE_STATUS, status, 0)
            glGetError()
            if (GL_TRUE != status[0]) {
                throw GlException("Cannot compile fragment shader: ${glGetShaderInfoLog(it)}")
            }
        }

        program.also {
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
            glGetProgramiv(it, GL_LINK_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw GlException("Cannot link program: ${glGetProgramInfoLog(it)}")
            }
            glValidateProgram(it)
            glGetProgramiv(it, GL_VALIDATE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw GlException("Cannot validate program: ${glGetProgramInfoLog(it)}")
            }
            glUseProgram(it)
        }

        positionAttributeLocation = glGetAttribLocation(program, "position")
        colorAttributeLocation = glGetAttribLocation(program, "color")
        modelIndexAttributeLocation = glGetAttribLocation(program, "modelIndex")
        modelMatrixLocation = glGetUniformLocation(program, "M")
        viewMatrixLocation = glGetUniformLocation(program, "V")
        projectionMatrixLocation = glGetUniformLocation(program, "P")

        if (positionAttributeLocation < 0) throw GlException("Cannot locate position attribute")
        if (colorAttributeLocation < 0) throw GlException("Cannot locate color attribute")
        if (modelIndexAttributeLocation < 0) throw GlException("Cannot locate model index attribute")
        if (modelMatrixLocation < 0) throw GlException("Cannot locate model matrix uniform")
        if (viewMatrixLocation < 0) throw GlException("Cannot locate view matrix uniform")
        if (projectionMatrixLocation < 0) throw GlException("Cannot locate projection matrix uniform")

        checkGlError("Initialization")
    }

    /**
     * Upload the first [uploadCounts] matrices from [buffer] into the model matrix uniform array.
     */
    fun uploadModelMatrixBuffer(buffer: RandomAccessBuffer, uploadCounts: Int) {
        glUniformMatrix4fv(modelMatrixLocation, uploadCounts, false, buffer.floatBuffer)
        checkGlError("Uploading model buffer")
    }

    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.floats.floatBuffer)
        checkGlError("Uploading view matrix")
    }

    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.floats.floatBuffer)
        checkGlError("Uploading projection matrix")
    }

    /**
     * Simply checks for an error-state and throws [GlException] if so.
     * @param currentAction Short description what the caller is currently about to do.
     */
    private fun checkGlError(currentAction: String) {
        if (glGetError() != GL_NO_ERROR)
            throw GlException("Occurred when: $currentAction")
    }

}
