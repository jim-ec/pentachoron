package io.jim.tesserapp.entity

import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test

class EntityBufferTest {

    private val entityBuffer = EntityBuffer("Cube", "Label", "Axis")
    private val cube = entityBuffer["Cube"]
    private val label = entityBuffer["Label"]
    private val axis = entityBuffer["Axis"]

    @Test
    fun implicitRootEntity() {
        assertEquals(entityBuffer["Root"], entityBuffer.root)
    }

    @Test(expected = NoSuchEntityException::class)
    fun throwOnGettingNonExistentEntity() {
        entityBuffer["Foo"]
    }

    @Test(expected = NoSuchEntityException::class)
    fun removeNonParentingChild() {
        cube.removeChild(axis)
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
