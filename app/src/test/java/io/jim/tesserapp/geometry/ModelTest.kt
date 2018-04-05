package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test

class ModelTest {

    @Test
    fun creation() {
        Model(
                Color.BLACK,
                arrayOf(
                        Vector(0.0, 0.0, 0.0, 0.0),
                        Vector(1.0, 0.0, 0.0, 0.0),
                        Vector(1.0, 1.0, 0.0, 0.0)
                ),
                arrayOf(
                        Line(0, 1),
                        Line(1, 2),
                        Line(2, 0)
                )
        ).apply {
            assertEquals(Color.BLACK, color)
            assertEquals(3, points.size)
            assertEquals(3, lines.size)
        }
    }

}