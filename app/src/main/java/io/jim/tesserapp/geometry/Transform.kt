package io.jim.tesserapp.geometry

data class Transform(
        val rotationX: Double = 0.0,
        val rotationY: Double = 0.0,
        val rotationZ: Double = 0.0,
        val rotationQ: Double = 0.0,
        val translationX: Double = 0.0,
        val translationY: Double = 0.0,
        val translationZ: Double = 0.0,
        val translationQ: Double = 0.0
) {
    
    val data = doubleArrayOf(
            rotationX,
            rotationY,
            rotationZ,
            rotationQ,
            translationX,
            translationY,
            translationZ,
            translationQ
    )
    
}