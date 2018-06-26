package io.jim.tesserapp.math

/**
 * Represents a cubic polynomial function.
 * @constructor Create the function mapping `x ↦ a(x - x0)³ + b(x - x0)² + c(x - x0) + y0`.
 */
data class CubicPolynomial(
        private var a: Double = 0.0,
        private var b: Double = 0.0,
        private var c: Double = 0.0,
        private var x0: Double = 0.0,
        private var y0: Double = 0.0
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
     * @throws RuntimeException If x-difference between source and target would is zero.
     */
    fun span(
            sourceX: Double,
            sourceY: Double,
            targetX: Double,
            targetY: Double,
            sourceGradient: Double
    ) {
        if (sourceX == targetX)
            throw RuntimeException("X-range of curve cannot be zero")
        
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
     * @throws RuntimeException If x-difference between source and target would is zero.
     */
    fun reSpan(
            sourceX: Double,
            targetX: Double,
            targetY: Double,
            keepSourceGradient: Boolean
    ) {
        span(
                sourceX = sourceX,
                sourceY = this(sourceX),
                targetX = targetX,
                targetY = targetY,
                sourceGradient = if (keepSourceGradient) derivation(sourceX) else 0.0
        )
    }
    
    /**
     * Compute `f([x])`.
     */
    operator fun invoke(x: Double) =
            (x - x0).let { dx -> a * dx * dx * dx + b * dx * dx + c * dx + y0 }
    
    /**
     * Compute the derivation `f'([x])`.
     */
    fun derivation(x: Double) =
            (x - x0).let { dx -> 3 * a * dx * dx + 2 * b * dx + c }
    
}
