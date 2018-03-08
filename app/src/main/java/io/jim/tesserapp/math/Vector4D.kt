package io.jim.tesserapp.math

class Vector4D(x: Double, y: Double, z: Double, w: Double)
    : Vector(doubleArrayOf(x, y, z, w)) {

    val x get() = this[0]
    val y get() = this[1]
    val z get() = this[2]
    val w get() = this[3]

    override val orthographicProjection
        get() = Vector3D(x, y, z)

}
