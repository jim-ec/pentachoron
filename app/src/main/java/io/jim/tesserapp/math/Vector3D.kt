package io.jim.tesserapp.math

class Vector3D(x: Double, y: Double, z: Double)
    : Vector(doubleArrayOf(x, y, z)) {

    val x get() = this[0]
    val y get() = this[1]
    val z get() = this[2]

    override val orthographicProjection
        get() = Vector2D(x, y)

    infix fun cross(v: Vector3D): Vector3D {
        return Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }

}
