/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.geometry

fun gridOmitAxisIndicator(): List<Line> {
    
    val linesPerUnit = 1
    val gridStartIndex = -5 * linesPerUnit
    val gridEndIndex = 5 * linesPerUnit
    
    fun vector(x: Int, y: Int) = Position(x.toDouble() / linesPerUnit, 0.0, y.toDouble() / linesPerUnit, 0.0)
    
    val xLines = (gridStartIndex..gridEndIndex).flatMap { y ->
        (gridStartIndex until gridEndIndex).mapNotNull { x ->
            if (y == 0 && x >= 0 && x < linesPerUnit) null
            else Line(vector(x, y), vector(x + 1, y))
        }
    }
    
    val zLines = (gridStartIndex..gridEndIndex).flatMap { x ->
        (gridStartIndex until gridEndIndex).mapNotNull { y ->
            if (x == 0 && y >= 0 && y < linesPerUnit) null
            else Line(vector(x, y), vector(x, y + 1))
        }
    }
    
    return xLines + zLines
}
