package io.jim.tesserapp.math

data class ViewMatrix(
        var distance: Float = 1f,
        var aspectRatio: Float = 1f,
        var horizontalRotation: Float = 0f,
        var verticalRotation: Float = 0f
) {

    val buffer = MatrixBuffer(10)
    private val matricesMemory = buffer.MemorySpace()

    private val matrixRotation = 2
    private val matrixHorizontalRotation = 3
    private val matrixVerticalRotation = 4
    private val matrixLookAt = 5
    private val matrixScale = 6

    fun compute() {
        matricesMemory.apply {
            rotation(matrixHorizontalRotation, 2, 0, horizontalRotation)
            rotation(matrixVerticalRotation, 0, 1, verticalRotation)
            multiply(matrixHorizontalRotation, matrixVerticalRotation, matrixRotation)

            lookAt(matrixLookAt, Vector(distance, 0f, 0f, 1f), Vector(0f, 0f, 0f, 1f), Vector(0f, 1f, 0f, 1f))
            scale(matrixScale, Vector(1f, aspectRatio, 1f, 1f))

            multiply(matrixRotation, matrixLookAt, 1)
            multiply(1, matrixScale, 0)
        }
    }

}
