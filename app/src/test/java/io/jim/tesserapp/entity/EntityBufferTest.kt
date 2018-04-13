package io.jim.tesserapp.entity

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test

class EntityBufferTest {

    private val entityBuffer = EntityBuffer(
            Triple("Label", Geometry::class, listOf(Color.BLACK, arrayOf<Vector>(), arrayOf<Line>())),
            Triple("Axis", Entity::class, listOf()),
            Triple("Cube", ExtrudedGeometry::class, listOf(Color.BLACK, arrayOf<Vector>(), arrayOf<Line>(), Vector(0.0, 0.0, 1.0, 0.0)))
    )

    private val cube = entityBuffer["Cube"]
    private val axis = entityBuffer["Axis"]
    private val label = entityBuffer["Label"]

    @Test
    fun implicitRootEntity() {
        assertEquals(entityBuffer["Root"], entityBuffer.root)
    }

    @Test(expected = EntityBuffer.NoSuchEntityException::class)
    fun throwOnGettingNonExistentEntity() {
        entityBuffer["Foo"]
    }

    @Test(expected = Entity.NoSuchChildException::class)
    fun removeNonParentingChild() {
        cube.removeChild(axis)
    }

    @Test
    fun correctClasses() {
        assertEquals(Entity::class, entityBuffer.root::class)
        assertEquals(Entity::class, axis::class)
        assertEquals(Geometry::class, label::class)
        assertEquals(ExtrudedGeometry::class, cube::class)
    }

    @Test
    fun implicitRootParenting() {
        assertEquals("Root must have 3 children", 3, entityBuffer["Root"].count())
    }

    @Test
    fun parentedTransformOverOneLevel() {
        entityBuffer.root.translation(Vector(1.0, 0.0, 0.0, 1.0))
    }

}
