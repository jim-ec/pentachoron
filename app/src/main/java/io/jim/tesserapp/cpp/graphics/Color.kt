package io.jim.tesserapp.cpp.graphics

data class Color(
        val red: Float,
        val green: Float,
        val blue: Float
) {
    
    constructor(colorInt: Int) : this(
            red = (colorInt shr 16 and 0xFF).toFloat() / 255f,
            green = (colorInt shr 8 and 0xFF).toFloat() / 255f,
            blue = (colorInt and 0xFF).toFloat() / 255f
    )
    
    companion object {
        
        val BLACK = Color(0f, 0f, 0f)
        
    }
    
}
