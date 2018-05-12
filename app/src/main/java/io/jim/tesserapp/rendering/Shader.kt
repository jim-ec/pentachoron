package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.util.RandomAccessBuffer

/**
 * A shader pipeline with a vertex shader, fragment shader an locations of all attributes and
 * uniforms.
 */
class Shader {

    /**
     * Thrown upon OpenGL errors.
     */
    inner class GlException(msg: String, error: Int = GLES30.glGetError()) :
            RuntimeException("OpenGL Error 0x$error (${errorString(error)}): $msg")

    /**
     * Return a string describing the current OpenGL error.
     */
    private fun errorString(error: Int) =
            when (error) {
                GLES30.GL_NO_ERROR -> "no error"
                GLES30.GL_INVALID_ENUM -> "invalid enumeration"
                GLES30.GL_INVALID_VALUE -> "invalid value"
                GLES30.GL_INVALID_OPERATION -> "invalid operation"
                GLES30.GL_INVALID_FRAMEBUFFER_OPERATION -> "invalid framebuffer operation"
                GLES30.GL_OUT_OF_MEMORY -> "out of memory"
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

    private val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
    private val fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
    private val program = GLES30.glCreateProgram()
    private val viewMatrixLocation: Int
    private val projectionMatrixLocation: Int
    private val modelMatrixLocation: Int

    companion object {

        private val vertexShaderSource = """
            #version 300 es

            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M[100];

            in vec3 position;
            in vec3 color;
            in float modelIndex;

            out vec3 vColor;

            void main() {
                gl_Position = P * V * M[int(modelIndex)] * vec4(position, 1.0);
                vColor = color;
            }

            """.trim()

        private val fragmentShaderSource = """
            #version 300 es

            in mediump vec3 vColor;

            out mediump vec4 fColor;

            void main() {
                fColor = vec4(vColor, 1.0);
            }

            """.trim()

    }

    init {
        if (vertexShader < 0) throw GlException("Cannot create vertex shader")
        if (fragmentShader < 0) throw GlException("Cannot create fragment shader")
        if (program < 0) throw GlException("Cannot create shader program")

        vertexShader.also {
            GLES30.glShaderSource(it, vertexShaderSource)
            GLES30.glCompileShader(it)

            GLES30.glGetShaderiv(it, GLES30.GL_COMPILE_STATUS, resultCode)
            if (GLES30.GL_TRUE != resultCode()) {
                throw GlException("Cannot compile vertex shader: ${GLES30.glGetShaderInfoLog(it)}")
            }
        }

        fragmentShader.also {
            GLES30.glShaderSource(it, fragmentShaderSource)
            GLES30.glCompileShader(it)
            GLES30.glGetShaderiv(it, GLES30.GL_COMPILE_STATUS, resultCode)

            if (GLES30.GL_TRUE != resultCode()) {
                throw GlException("Cannot compile fragment shader: ${GLES30.glGetShaderInfoLog(it)}")
            }
        }

        program.also {
            // Link program together:
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
            GLES30.glGetProgramiv(it, GLES30.GL_LINK_STATUS, resultCode)
            if (GLES30.GL_TRUE != resultCode()) {
                throw GlException("Cannot link program: ${GLES30.glGetProgramInfoLog(it)}")
            }

            // Validate program:
            GLES30.glValidateProgram(it)
            GLES30.glGetProgramiv(it, GLES30.GL_VALIDATE_STATUS, resultCode)
            if (GLES30.GL_TRUE != resultCode()) {
                throw GlException("Cannot validate program: ${GLES30.glGetProgramInfoLog(it)}")
            }

            // Use the program for further draw calls:
            GLES30.glUseProgram(it)
        }

        // Retrieve attribute locations:
        positionAttributeLocation = GLES30.glGetAttribLocation(program, "position")
        colorAttributeLocation = GLES30.glGetAttribLocation(program, "color")
        modelIndexAttributeLocation = GLES30.glGetAttribLocation(program, "modelIndex")

        // Retrieve uniform locations:
        viewMatrixLocation = GLES30.glGetUniformLocation(program, "V")
        projectionMatrixLocation = GLES30.glGetUniformLocation(program, "P")
        modelMatrixLocation = GLES30.glGetUniformLocation(program, "M")

        checkGlError("Initialization")
    }

    /**
     * Upload the first [uploadCounts] matrices from [buffer] into the model matrix uniform array.
     */
    fun uploadModelMatrixBuffer(buffer: RandomAccessBuffer, uploadCounts: Int) {
        GLES30.glUniformMatrix4fv(modelMatrixLocation, uploadCounts, false, buffer.floatBuffer)
        checkGlError("Uploading model buffer")
    }

    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.floats.floatBuffer)
        checkGlError("Uploading view matrix")
    }

    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.floats.floatBuffer)
        checkGlError("Uploading projection matrix")
    }

    /**
     * Simply checks for an error-state and throws [GlException] if so.
     * @param currentAction Short description what the caller is currently about to do.
     */
    private fun checkGlError(currentAction: String) {
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR)
            throw GlException("Occurred when: $currentAction", error)
    }

}
