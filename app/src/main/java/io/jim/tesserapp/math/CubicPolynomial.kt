package io.jim.tesserapp.math

/**
 * Represents a shift-able cubic polynomial function.
 * @constructor Create the function mapping `x ↦ a(x - x0)³ + b(x - x0)² + c(x - x0) + y0`.
 */
data class CubicPolynomial(
        private var a: Float = 0f,
        private var b: Float = 0f,
        private var c: Float = 0f,
        private var x0: Float = 0f,
        private var y0: Float = 0f
) {

    /**
     * Spans the curve from a source point to a target point.
     *
     * The resulting curve has a specific start slope and is flat at its target.
     *
     * @param sourceGradient Curve slope at the source point.
     * @param sourceX Source point, x component.
     * @param sourceY Source point, y component.
     * @param targetX Target point, x component.
     * @param targetY Target point, y component.
     * @throws MathException If x-difference between source and target would is zero.
     */
    fun span(
            sourceX: Float,
            sourceY: Float,
            targetX: Float,
            targetY: Float,
            sourceGradient: Float
    ) {
        if (sourceX == targetX)
            throw MathException("X-range of curve cannot be zero")

        val dx = targetX - sourceX
        val dy = targetY - sourceY

        a = (sourceGradient * dx - 2 * dy) / (dx * dx * dx)
        b = (3 * dy - 2 * sourceGradient * dx) / (dx * dx)
        c = sourceGradient
        x0 = sourceX
        y0 = sourceY
    }

    /**
     * Re-spans the curve to a new point from a source point on the old curve.
     *
     * The new curve will be flat at the new target.
     *
     * @param sourceX Point on the old curve the new will one originating from.
     * @param targetX Target point of new curve, x-component.
     * @param targetY Target point of new curve, y-component.
     * @param keepSourceGradient Whether the new curve should keep its gradient at its source.
     * @throws MathException If x-difference between source and target would is zero.
     */
    fun reSpan(
            sourceX: Float,
            targetX: Float,
            targetY: Float,
            keepSourceGradient: Boolean
    ) {
        span(
                sourceX = sourceX,
                sourceY = this(sourceX),
                targetX = targetX,
                targetY = targetY,
                sourceGradient = if (keepSourceGradient) derivation(sourceX) else 0.0f
        )
    }

    /**
     * Compute `f([x])`.
     */
    operator fun invoke(x: Float): Float {
        val dx = x - x0
        return a * dx * dx * dx + b * dx * dx + c * dx + y0
    }

    /**
     * Compute the derivation `f'([x])`.
     */
    fun derivation(x: Float): Float {
        val dx = x - x0
        return 3 * a * dx * dx + 2 * b * dx + c
    }

}
