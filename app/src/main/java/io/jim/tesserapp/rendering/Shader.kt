package io.jim.tesserapp.rendering

import android.opengl.GLES10.GL_STACK_OVERFLOW
import android.opengl.GLES10.GL_STACK_UNDERFLOW
import android.opengl.GLES30.*
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
    inner class GlException(msg: String, error: Int = glGetError()) :
            RuntimeException("OpenGL Error 0x$error (${errorString(error)}): $msg")

    /**
     * Return a string describing the current OpenGL error.
     */
    private fun errorString(error: Int) =
            when (error) {
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
    private val viewMatrixLocation: Int
    private val projectionMatrixLocation: Int

    private val modelMatrixCountsLocation: Int
    private val modelMatrixTexture = resultCode { glGenTextures(1, resultCode) }

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
        if (positionAttributeLocation < 0) throw GlException("Cannot locate position attribute")

        colorAttributeLocation = glGetAttribLocation(program, "color")
        if (colorAttributeLocation < 0) throw GlException("Cannot locate color attribute")

        modelIndexAttributeLocation = glGetAttribLocation(program, "modelIndex")
        if (modelIndexAttributeLocation < 0) throw GlException("Cannot locate model index attribute")

        viewMatrixLocation = glGetUniformLocation(program, "V")
        if (viewMatrixLocation < 0) throw GlException("Cannot locate view matrix uniform")

        projectionMatrixLocation = glGetUniformLocation(program, "P")
        if (projectionMatrixLocation < 0) throw GlException("Cannot locate projection matrix uniform")

        modelMatrixCountsLocation = glGetUniformLocation(program, "modelMatrixCounts")
        if (modelMatrixCountsLocation < 0) throw GlException("Cannot locate model matrix counts uniform")

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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        println("Initialize model-matrix texture for $modelMatrixCounts matrices")

        glTexImage2D(
                GL_TEXTURE_2D,              // target
                0,                          // level
                GL_RGBA,                    // internal format
                modelMatrixCounts * 4,      //  width
                1,                          // height
                0,                          // border
                GL_RGBA,                    // format
                GL_FLOAT,                   // type
                null                        // pixels
        )

        checkGlError("Initialize model-matrix texture")
    }

    /**
     * Upload all matrices from [buffer] into the model matrix texture.
     */
    fun uploadModelMatrixBuffer(buffer: RandomAccessBuffer) {
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
        val error = glGetError()
        if (error != GL_NO_ERROR)
            throw GlException("Occurred when: $currentAction", error)
    }

}
