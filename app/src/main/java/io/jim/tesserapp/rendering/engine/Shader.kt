package io.jim.tesserapp.rendering.engine

import android.opengl.GLES20
import android.opengl.GLES30
import io.jim.tesserapp.rendering.resultCode

class Shader(val type: Int, source: String) {

    val handle = GLES20.glCreateShader(type)

    init {
        GLES30.glShaderSource(handle, source)
        GLES30.glCompileShader(handle)

        GLES30.glGetShaderiv(handle, GLES30.GL_COMPILE_STATUS, resultCode)
        if (GLES30.GL_TRUE != resultCode()) {
            throw GlException("Cannot compile vertex shader: ${GLES30.glGetShaderInfoLog(handle)}")
        }
    }

}
