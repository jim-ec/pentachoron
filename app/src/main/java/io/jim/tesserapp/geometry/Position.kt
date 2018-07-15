/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.geometry

data class Position(
        val x: Double,
        val y: Double,
        val z: Double,
        val q: Double
) {
    
    /**
     * Return this vector component-wise added to [rhs].
     */
    operator fun plus(rhs: Position) = Position(
            x + rhs.x,
            y + rhs.y,
            z + rhs.z,
            q + rhs.q
    )
    
}
