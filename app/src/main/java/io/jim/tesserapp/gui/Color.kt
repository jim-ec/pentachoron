package io.jim.tesserapp.gui

@Suppress("unused")
data class Color(val red: Float, val green: Float, val blue: Float) {

    companion object {
        val RED = Color(1f, 0f, 0f)
        val GREEN = Color(0f, 1f, 0f)
        val BLUE = Color(0f, 0f, 1f)
        val WHITE = Color(1f, 1f, 1f)
        val BLACK = Color(0f, 0f, 0f)
    }

}
