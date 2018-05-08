package io.jim.tesserapp.math.vector

/**
 * A homogeneous 3d vector.
 */
open class Vector3d : VectorN(3) {

    /**
     * X-component.
     */
    var x by IndexAlias(0)

    /**
     * Y-component.
     */
    var y by IndexAlias(1)

    /**
     * Z-component.
     */
    var z by IndexAlias(2)

    /**
     * Compute the vector product of [lhs] and [rhs], storing the result in this vector.
     */
    fun crossed(lhs: Vector3d, rhs: Vector3d) {
        x = lhs.y * rhs.z - lhs.z * rhs.y
        y = lhs.z * rhs.x - lhs.x * rhs.z
        z = lhs.x * rhs.y - lhs.y * rhs.x
    }

}
