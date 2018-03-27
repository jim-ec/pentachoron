package io.jim.tesserapp.math

import junit.framework.Assert.assertEquals
import kotlin.math.atan2
import kotlin.math.sqrt

class SphericalCoordinate(v: Vector) {

    /**
     * Represents the polar angle, in radians.
     */
    val theta: Double

    /**
     * Represents the azimuth angle, in radians.
     */
    val phi: Double

    /**
     * Represents length of vector.
     */
    val r: Double

    init {
        assertEquals(v.dimension, 3)
        r = v.length
        theta = atan2(sqrt(v.x * v.x + v.y * v.y), v.z)
        phi = atan2(v.y, v.x)
    }

    override fun toString(): String {
        return "(θ=$theta, φ=$phi}"
    }

}
