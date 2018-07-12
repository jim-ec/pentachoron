package io.jim.tesserapp.graphics

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
    
    val encoded: Int
        get() {
            return ((red * 255).toInt() shl 16) or
                    ((green * 255).toInt() shl 8) or
                    (blue * 255).toInt()
        }
    
    companion object {
        
        val BLACK = Color(0f, 0f, 0f)
        
    }
    
}
