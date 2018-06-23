package io.jim.tesserapp.math.matrix

/**
 * Load a 3D to 2D perspective matrix.
 * The z component gets remapping between a near and far value.
 * @param near Near plane. If Vector lies on that plane (negated), it will be projected to 0.
 * @param far Far plane. If Vector lies on that plane (negated), it will be projected to 1.
 */
fun perspective(near: Double, far: Double) =
        Matrix(4, 4, mapOf(
                2 to 3 to -1.0,
                3 to 3 to 0.0,
                2 to 2 to -far / (far - near),
                3 to 2 to -(far * near) / (far - near)
        ))
