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

    private val modelMatrixCountsLocation: Int
    private val modelMatrixTexture = resultCode { GLES30.glGenTextures(1, resultCode) }

    @Suppress("MemberVisibilityCanBePrivate")
    var modelMatrixCounts = 10
        set(value) {
            field = value
            initializeModelMatrixTexture()
        }

    private val vertexShaderSource = """
            uniform mat4 P;
            uniform mat4 V;

            uniform float modelMatrixCounts;
            uniform sampler2D modelMatrixTexture;

            attribute vec3 position;
            attribute vec3 color;
            attribute float modelIndex;

            varying vec3 vColor;

            /**
             * Fetch one model matrix row from the model matrix texture.
             * @param row Row to be loaded. Must be within [0.0, 4.0[.
             */
            vec4 fetchModelMatrixCoefficient(in float row) {
                float xOffset = 1.0 / (2.0 * 4.0 * modelMatrixCounts);
                float x = (4.0 * modelIndex + row) / (4.0 * modelMatrixCounts);
                float y = 0.5;
                return texture2D(modelMatrixTexture, vec2(xOffset + x, y));
            }

            void main() {

                // Build model matrix by loading data from  texture:
                mat4 modelMatrix;
                modelMatrix[0] = fetchModelMatrixCoefficient(0.0);
                modelMatrix[1] = fetchModelMatrixCoefficient(1.0);
                modelMatrix[2] = fetchModelMatrixCoefficient(2.0);
                modelMatrix[3] = fetchModelMatrixCoefficient(3.0);

                gl_Position = P * V * modelMatrix * vec4(position, 1.0);
                vColor = color;
            }
        """.trim()

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
        modelMatrixCountsLocation = GLES30.glGetUniformLocation(program, "modelMatrixCounts")

        checkGlError("Initialization")

        initializeModelMatrixTexture()
    }

    /**
     * Always called when [modelMatrixCounts] changes.
     * Allocates and instructs the model matrix texture.
     */
    private fun initializeModelMatrixTexture() {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, modelMatrixTexture)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)

        println("Initialize model-matrix texture for $modelMatrixCounts matrices")

        GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D,       // target
                0,                          // level
                GLES30.GL_RGBA,             // internal format
                modelMatrixCounts * 4,      //  width
                1,                          // height
                0,                          // border
                GLES30.GL_RGBA,             // format
                GLES30.GL_FLOAT,            // type
                null                        // pixels
        )

        checkGlError("Initialize model-matrix texture")
    }

    /**
     * Upload all matrices from [buffer] into the model matrix texture.
     */
    fun uploadModelMatrixBuffer(buffer: RandomAccessBuffer) {
        GLES30.glUniform1f(modelMatrixCountsLocation, modelMatrixCounts.toFloat())

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, modelMatrixTexture)
        GLES30.glTexSubImage2D(
                GLES30.GL_TEXTURE_2D,
                0,
                0, 0,
                modelMatrixCounts * 4,
                1,
                GLES30.GL_RGBA, GLES30.GL_FLOAT,
                buffer.floatBuffer
        )
        checkGlError("Write model matrices to texture")
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
