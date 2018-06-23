package io.jim.tesserapp.math.matrix

import kotlin.math.cos
import kotlin.math.sin

typealias Radians = Double

enum class RotationPlane(inline val a: Int, inline val b: Int) {
    YZ(1, 2),
    XZ(2, 0),
    XY(0, 1),
    XQ(0, 3)
}

fun rotation(size: Int, plane: RotationPlane, radians: Radians) =
        identity(size, values = mapOf(
                plane.a to plane.a to cos(radians),
                plane.a to plane.b to sin(radians),
                plane.b to plane.a to -sin(radians),
                plane.b to plane.b to cos(radians)
        ))
