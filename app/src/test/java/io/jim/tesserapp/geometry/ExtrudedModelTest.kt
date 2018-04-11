package io.jim.tesserapp.geometry

import io.jim.tesserapp.entity.ExtrudedModel
import io.jim.tesserapp.entity.Line
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test

class ExtrudedModelTest {

    @Test
    fun creation() {
        ExtrudedModel(
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
                ),
                Vector(0.0, 0.0, 1.0, 0.0)
        ).apply {
            assertEquals(3 + 3, points.size)
            assertEquals(3 + 3 + 3, lines.size)
            assertEquals(0 until 3, original)
            assertEquals(3 until 6, replicate)
        }
    }

}
