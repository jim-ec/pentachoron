package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.common.SmoothTimedValueDelegate
import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.math.vector.Vector4dh
import io.jim.tesserapp.ui.controllers.Rotatable
import io.jim.tesserapp.ui.controllers.Translatable
import io.jim.tesserapp.util.Callback

/**
 * A geometrical structure consisting of vertices.
 */
open class Geometry(

        /**
         * This geometry's name.
         */
        val name: String,

        /**
         * Color of this geometry.
         */
        val baseColor: Color = Color.BLACK

) {

    /**
     * List containing all positions.
     */
    val positions = ArrayList<Vector4dh>()

    /**
     * List containing all lines constructed from [positions] using indices.
     */
    val lines = ArrayList<LineIndices>()

    /**
     * This geometries model matrix.
     * [computeModelMatrix] must be called in order to keep the model matrix up-to-date.
     */
    val modelMatrix = Matrix(5)

    private val rotationMatrixY = Matrix(5)
    private val rotationMatrixX = Matrix(5)
    private val rotationMatrixZ = Matrix(5)
    private val rotationMatrixZY = Matrix(5)
    private val rotationMatrix = Matrix(5)
    private val translationMatrix = Matrix(5)

    /**
     * Smooth rotation around the x, y and z axis.
     * Final rotation is component-wise summed up from [rotation] and [smoothRotation].
     */
    val smoothRotation = object : Rotatable {
        override var x by SmoothTimedValueDelegate<Rotatable>(0f, 200L)
        override var y by SmoothTimedValueDelegate<Rotatable>(0f, 200L)
        override var z by SmoothTimedValueDelegate<Rotatable>(0f, 200L)
        override var q by SmoothTimedValueDelegate<Rotatable>(0f, 200L)
    }

    /**
     * Smooth translation.
     * Final translation is component-wise summed up from [translation] and [smoothTranslation].
     */
    val smoothTranslation = object : Translatable {
        override var x by SmoothTimedValueDelegate<Translatable>(0f, 200L)
        override var y by SmoothTimedValueDelegate<Translatable>(0f, 200L)
        override var z by SmoothTimedValueDelegate<Translatable>(0f, 200L)
        override var q by SmoothTimedValueDelegate<Translatable>(0f, 200L)
    }

    /**
     * Rotation. Unlike [smoothRotation] this rotation is not smoothed.
     * Final rotation is component-wise summed up from [rotation] and [smoothRotation].
     */
    val rotation = Vector4dh()

    /**
     * Translation. Unlike [smoothTranslation] this translation vector is not smoothed.
     * Final translation is component-wise summed up from [translation] and [smoothTranslation].
     */
    val translation = Vector4dh()

    private val translationVector = Vector4dh()

    /**
     * Invoke [f] for each position and the color it's associated with.
     */
    inline fun forEachVertex(f: (position: Vector4dh, color: Color) -> Unit) {
        lines.forEach {
            f(positions[it.from], it.color)
            f(positions[it.to], it.color)
        }
    }

    /**
     * Listeners are fired every time a single point or line is added.
     */
    val onGeometryChanged = Callback()

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    protected fun addPosition(position: Vector4dh) {
        positions += position
        onGeometryChanged()
    }

    /**
     * Add a lines from point [a] to point [b].
     */
    protected fun addLine(a: Int, b: Int, color: Color = baseColor) {
        lines += LineIndices(a, b, color)
        onGeometryChanged()
    }

    /**
     * Colorize the [lineIndex]th line to [color].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun colorizeLine(lineIndex: Int, color: Color) {
        lines[lineIndex].color = color
        onGeometryChanged()
    }

    /**
     * Colorize the [lineIndex]th line to [baseColor].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("unused")
    fun decolorizeLine(lineIndex: Int) {
        colorizeLine(lineIndex, baseColor)
    }

    /**
     * Remove all geometry data.
     */
    protected fun clearGeometry() {
        positions.clear()
        lines.clear()
        onGeometryChanged()
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     * @param keepColors The generated copy will have matching colors to the line set it originated from.
     * @param connectorColor Color of the lines connecting the original and generated lines.
     */
    fun extrude(
            direction: Vector4dh,
            keepColors: Boolean = false,
            connectorColor: Color = baseColor
    ) {
        val size = positions.size

        for (i in 0 until size) {
            positions += Vector4dh().apply {
                copyFrom(positions[i])
                this += direction
            }
        }

        lines += lines.map {
            LineIndices(
                    it.from + size,
                    it.to + size,
                    if (keepColors) it.color else baseColor
            )
        }
        for (i in 0 until size) {
            addLine(i, i + size, connectorColor)
        }
    }

    /**
     * Recomputes all model matrices recursively.
     *
     * Since this recursive function computes model matrices for its children after it's done
     * with its one global matrix, it can safely access its parent's global model matrix,
     * since that is guaranteed to already be computed.
     */
    fun computeModelMatrix() {

        // Rotation:
        rotationMatrixX.rotation(a = 1, b = 2, radians = rotation.x + smoothRotation.x)
        rotationMatrixY.rotation(a = 2, b = 0, radians = rotation.y + smoothRotation.y)
        rotationMatrixZ.rotation(a = 0, b = 1, radians = rotation.z + smoothRotation.z)

        rotationMatrixZY.multiplication(
                rhs = rotationMatrixZ,
                lhs = rotationMatrixY
        )

        rotationMatrix.multiplication(
                lhs = rotationMatrixZY,
                rhs = rotationMatrixX
        )

        // Translation:
        translationVector.load(
                translation.x + smoothTranslation.x,
                translation.y + smoothTranslation.y,
                translation.z + smoothTranslation.z,
                translation.q + smoothTranslation.q
        )
        translationMatrix.translation(translationVector)

        // Model transform:
        modelMatrix.multiplication(
                lhs = rotationMatrix,
                rhs = translationMatrix
        )

    }

    override fun toString() = name

}
