/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.graphics.gl

/**
 * A RGB color.
 */
class Color(
        val code: Int
) {
    
    /**
     * Return red component, normalized between 0 and 1.
     */
    val red: Float
        get() = (code shr 16 and 0xFF).toFloat() / 255f
    
    /**
     * Return green component, normalized between 0 and 1.
     */
    val green: Float
        get() = (code shr 8 and 0xFF).toFloat() / 255f
    
    /**
     * Return blue component, normalized between 0 and 1.
     */
    val blue: Float
        get() = (code and 0xFF).toFloat() / 255f
    
    companion object {
        
        /**
         * Black color.
         * Often used as the "default" color.
         */
        val BLACK = Color(0x000000)
        
    }
}
