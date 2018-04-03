package io.jim.tesserapp

import io.jim.tesserapp.geometry.Lines
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Geometry unit test.
 */
class GeometryUnitTest {

    @Test
    fun extruding() {
        val geometry = Lines(3, Color(0f))
        geometry.addLine(Vector(1.0, 1.0, 0.0), Vector(2.0, 2.0, 0.0))
        geometry.extrude(Vector(0.0, 0.0, 1.0))
        assertEquals(4, geometry.points.size)
        assertEquals(4, geometry.lines.size)
        assertEquals(1.0, geometry.points.last().z, 0.1)
    }

}
