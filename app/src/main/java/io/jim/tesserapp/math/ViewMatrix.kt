package io.jim.tesserapp.math

data class ViewMatrix(
        var distance: Float,
        var aspectRatio: Float,
        var horizontalRotation: Float,
        var verticalRotation: Float
) {

    private val matrixLookAt = 5
    private val matrixScale = 6
    private val matrixRotation = 2
    private val matrixHorizontalRotation = 3
    private val matrixVerticalRotation = 4


}
