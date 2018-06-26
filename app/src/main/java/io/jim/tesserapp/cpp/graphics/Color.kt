package io.jim.tesserapp.cpp.graphics

data class Color(
        val red: Float,
        val green: Float,
        val blue: Float
) {
    
    constructor(colorInt: Int) : this(
            red = colorInt.red,
            green = colorInt.green,
            blue = colorInt.blue
    )
    
    companion object {
        
        val BLACK = Color(0f, 0f, 0f)
        
    }
    
}
