package io.jim.tesserapp

import io.jim.tesserapp.geometry.Line
import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Point
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Geometry unit test.
 */
class GeometryUnitTest {

    @Test
    fun extruding() {
        val geometry = Line(Point(1.0, 1.0, 0.0), Point(2.0, 2.0, 0.0), Color.BLACK)
        geometry.extrude(Direction(0.0, 0.0, 1.0))
        assertEquals(4, geometry.points.size)
        assertEquals(4, geometry.lines.size)
        assertEquals(1.0, geometry.points.last().z, 0.1)
    }

}
