package io.jim.tesserapp.math

/**
 * Represents a cubic polynomial function with a source, a target and a source gradient.
 */
data class CubicPolynomial(
        var a: Float,
        var b: Float,
        var c: Float,
        var x0: Float,
        var y0: Float
) {

    companion object {

        /**
         * Create a new curve with a specific positions and start gradient.
         */
        fun parametrized(
                sourceX: Float,
                sourceY: Float,
                targetX: Float,
                targetY: Float,
                sourceGradient: Float
        ) = let {
            val dx = targetX - sourceX
            val dy = targetY - sourceY
            if (dx == 0f)
                throw RuntimeException("X-range of curve cannot be zero")

            CubicPolynomial(
                    a = (sourceGradient * dx - 2 * dy) / (dx * dx * dx),
                    b = (3 * dy - 2 * sourceGradient * dx) / (dx * dx),
                    c = sourceGradient,
                    x0 = sourceX,
                    y0 = sourceY
            )
        }

    }

    /**
     * Retarget the curve to a new point from a source point on the old curve.
     * @param sourceX Point of old curve the new will source from.
     * @param targetX Target point of new curve.
     * @param targetY Target point of new curve.
     * @param keepSourceGradient Whether the new curve should keep its gradient at its source.
     * @throws RuntimeException If the x difference between target and source point is zero.
     */
    fun extent(
            sourceX: Float,
            targetX: Float,
            targetY: Float,
            keepSourceGradient: Boolean
    ) {
        val sourceY = this(sourceX)
        val dx = targetX - sourceX
        val dy = targetY - sourceY
        if (dx == 0f)
            throw RuntimeException("X-range of curve cannot be zero")

        val sourceGradient = if (keepSourceGradient) derivation(sourceX) else 0.0f

        a = (sourceGradient * dx - 2 * dy) / (dx * dx * dx)
        b = (3 * dy - 2 * sourceGradient * dx) / (dx * dx)
        c = sourceGradient
        x0 = sourceX
        y0 = sourceY
    }

    /**
     * Compute `f([x])`.
     */
    operator fun invoke(x: Float) =
            (fun(x: Float) = a * x * x * x + b * x * x + c * x)(x - x0) + y0

    /**
     * Compute the derivation `f'([x])`.
     */
    fun derivation(x: Float) =
            (fun(x: Float) = 3 * a * x * x + 2 * b * x + c)(x - x0)

}
