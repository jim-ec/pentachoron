package io.jim.tesserapp.gui

data class Color(val red: Float, val green: Float, val blue: Float) {

    constructor(greyScale: Float) : this(greyScale, greyScale, greyScale)

}
