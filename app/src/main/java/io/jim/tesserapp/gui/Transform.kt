package io.jim.tesserapp.gui

import android.opengl.Matrix

class Transform {

    private val view = FloatArray(16)
    private val projection = FloatArray(16)

    val transform: FloatArray
        get() = FloatArray(16).apply {
            Matrix.multiplyMM(this, 0, projection, 0, view, 0)
        }

    init {
        Matrix.setLookAtM(view, 0,
                5f, 5f, 5f, // Eye
                0f, 0f, 0f, // Center
                0f, 0f, 1f) // Up
    }

    fun setViewport(width: Int, height: Int) {
        Matrix.perspectiveM(projection, 0,
                60f, width.toFloat() / height,
                0.1f, 10f)
    }

}
