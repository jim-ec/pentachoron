package io.jim.tesserapp.rendering

import android.opengl.GLES10.GL_STACK_OVERFLOW
import android.opengl.GLES10.GL_STACK_UNDERFLOW
import android.opengl.GLES20.*
import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.util.RandomAccessBuffer

/**
 * A shader pipeline with a vertex shader, fragment shader an locations of all attributes and
 * uniforms.
 */
class Shader(
        initialModelMatrixCounts: Int = 10
) {

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

    private val modelMatrixCountsLocation: Int
    private val modelMatrixTexture = resultCode { glGenTextures(1, resultCode) }
    var modelMatrixCounts = initialModelMatrixCounts
        set(value) {
            field = value
            initializeModelMatrixTexture()
        }

    private val vertexShaderSource = """
            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M[$initialModelMatrixCounts];

            uniform float modelMatrixCounts;
            uniform sampler2D modelMatrixTexture;

            attribute vec3 position;
            attribute vec3 color;
            attribute float modelIndex;

            varying vec3 vColor;

            vec4 fetchModelMatrixCoefficient(in float row) {
                float xOffset = 1.0 / (2.0 * 4.0 * modelMatrixCounts);
                float x = (4.0 * modelIndex + row) / (4.0 * modelMatrixCounts);
                float y = 0.5;
                return texture2D(modelMatrixTexture, vec2(xOffset + x, y));
            }

            void main() {
                mat4 modelMatrix;
                modelMatrix[0] = fetchModelMatrixCoefficient(0.0);
                modelMatrix[1] = fetchModelMatrixCoefficient(1.0);
                modelMatrix[2] = fetchModelMatrixCoefficient(2.0);
                modelMatrix[3] = fetchModelMatrixCoefficient(3.0);

                //gl_Position = P * V * M[int(modelIndex)] * vec4(position, 1.0);
                gl_Position = P * V * modelMatrix * vec4(position, 1.0);
                vColor = color;
            }
        """.trim() // TODO: optimize calculations in fetchModelMatrixCoefficient()

    private val fragmentShaderSource = """
            varying mediump vec3 vColor;

            void main() {
                gl_FragColor = vec4(vColor, 1.0);
            }
        """.trim()

    init {
        if (vertexShader < 0) throw GlException("Cannot create vertex shader")
        if (fragmentShader < 0) throw GlException("Cannot create fragment shader")
        if (program < 0) throw GlException("Cannot create shader program")

        vertexShader.also {
            glShaderSource(it, vertexShaderSource)
            glCompileShader(it)

            glGetShaderiv(it, GL_COMPILE_STATUS, resultCode)
            if (GL_TRUE != resultCode()) {
                throw GlException("Cannot compile vertex shader: ${glGetShaderInfoLog(it)}")
            }
        }

        fragmentShader.also {
            glShaderSource(it, fragmentShaderSource)
            glCompileShader(it)
            glGetShaderiv(it, GL_COMPILE_STATUS, resultCode)
            glGetError()
            if (GL_TRUE != resultCode()) {
                throw GlException("Cannot compile fragment shader: ${glGetShaderInfoLog(it)}")
            }
        }

        program.also {
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
            glGetProgramiv(it, GL_LINK_STATUS, resultCode)
            if (GL_TRUE != resultCode()) {
                throw GlException("Cannot link program: ${glGetProgramInfoLog(it)}")
            }
            glValidateProgram(it)
            glGetProgramiv(it, GL_VALIDATE_STATUS, resultCode)
            if (GL_TRUE != resultCode()) {
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
        //if (modelMatrixLocation < 0) throw GlException("Cannot locate model matrix uniform")
        if (viewMatrixLocation < 0) throw GlException("Cannot locate view matrix uniform")
        if (projectionMatrixLocation < 0) throw GlException("Cannot locate projection matrix uniform")

        modelMatrixCountsLocation = glGetUniformLocation(program, "modelMatrixCounts")
        //TODO: if (modelMatrixCountsLocation < 0) throw GlException("Cannot locate model matrix counts uniform")

        checkGlError("Initialization")

        initializeModelMatrixTexture()
    }

    /**
     * Always called when [modelMatrixCounts] changes.
     * Allocates and instructs the model matrix texture.
     */
    private fun initializeModelMatrixTexture() {
        glBindTexture(GL_TEXTURE_2D, modelMatrixTexture)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        println("Initialize model-matrix texture for $modelMatrixCounts matrices")

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                modelMatrixCounts * 4,
                1,
                0,
                GL_RGBA,
                GL_FLOAT,
                null
        )

        checkGlError("Initialize model-matrix texture")
    }

    private fun checkTextureContents(modelIndex: Float, row: Float) {
        // float x = (4.0 * modelIndex + row) / (4.0 * modelMatrixCounts);
        // float y = 0.5;

        val x = (4f * modelIndex + row) / (4f * modelMatrixCounts)
    }

    /**
     * Upload the first [uploadCounts] matrices from [buffer] into the model matrix uniform array.
     */
    fun uploadModelMatrixBuffer(buffer: RandomAccessBuffer, uploadCounts: Int) {
        glUniformMatrix4fv(modelMatrixLocation, uploadCounts, false, buffer.floatBuffer)
        checkGlError("Uploading model buffer")

        glUniform1f(modelMatrixCountsLocation, modelMatrixCounts.toFloat())

        glBindTexture(GL_TEXTURE_2D, modelMatrixTexture)
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0, 0,
                modelMatrixCounts * 4,
                1,
                GL_RGBA, GL_FLOAT,
                buffer.floatBuffer
        )
        checkGlError("Write model matrices to texture")
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
