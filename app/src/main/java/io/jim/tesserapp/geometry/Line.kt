/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.geometry

data class Line(
        val start: Position,
        val end: Position
) {
    
    val points = listOf(start, end)
    
}
