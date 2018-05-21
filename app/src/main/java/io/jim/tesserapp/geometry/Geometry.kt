package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.common.Smoothed
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.vector.Vector3dh
import io.jim.tesserapp.math.vector.Vector4dh
import io.jim.tesserapp.ui.controllers.Rotatable
import io.jim.tesserapp.ui.controllers.Translatable

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

    companion object {

        /**
         * The volume onto which 4 dimensional vectors are projected.
         * This value should be well chosen, as no vector should ever have such a q-value,
         * since that will lead to projection into infinity.
         */
        const val Q_PROJECTION_VOLUME = 1.0

    }

    /**
     * List containing all positions.
     */
    val positions = ArrayList<Vector4dh>()

    /**
     * List containing all lines constructed from [positions] using indices.
     */
    val lines = ArrayList<Line<Vector4dh>>()

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
        override var x by Smoothed(0.0, 200.0)
        override var y by Smoothed(0.0, 200.0)
        override var z by Smoothed(0.0, 200.0)
        override var q by Smoothed(0.0, 200.0)
    }

    /**
     * Smooth translation.
     * Final translation is component-wise summed up from [translation] and [smoothTranslation].
     */
    val smoothTranslation = object : Translatable {
        override var x by Smoothed(0.0, 200.0)
        override var y by Smoothed(0.0, 200.0)
        override var z by Smoothed(0.0, 200.0)
        override var q by Smoothed(0.0, 200.0)
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

    override fun toString() = name

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    protected fun addPosition(position: Vector4dh) {
        positions += position
    }

    /**
     * Add a line spanning between two positions.
     * @param a Index to the start position.
     * @param b Index to the end position.
     * @param color Line color, defaults to geometry's [baseColor].
     */
    protected fun addLine(a: Int, b: Int, color: Color = baseColor) {
        lines += Line(positions, a, b, color)
    }

    /**
     * Add a line from position [a] to position [b].
     * This actually creates to new positions.
     * @param a Starting position.
     * @param b End position.
     * @param color Line color, defaults to geometry's [baseColor].
     */
    fun addLine(a: Vector4dh, b: Vector4dh, color: Color = baseColor) {
        addPosition(a)
        addPosition(b)
        addLine(positions.lastIndex - 1, positions.lastIndex, color)
    }

    /**
     * Add a quadrilateral with four corner and an optional color.
     * @param a First corner.
     * @param b Second corner.
     * @param c Third corner.
     * @param d Fourth corner.
     * @param color Color of added lines. Defaults to [baseColor].
     */
    fun addQuadrilateral(
            a: Vector4dh,
            b: Vector4dh,
            c: Vector4dh,
            d: Vector4dh,
            color: Color = baseColor
    ) {
        addPosition(a)
        addPosition(b)
        addPosition(c)
        addPosition(d)
        addLine(0, 1, color)
        addLine(1, 2, color)
        addLine(2, 3, color)
        addLine(3, 0, color)
    }

    /**
     * Colorize the [lineIndex]th line to [color].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun colorizeLine(lineIndex: Int, color: Color) {
        lines[lineIndex].color = color
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
            Line(
                    positions,
                    it.startIndex + size,
                    it.endIndex + size,
                    if (keepColors) it.color else baseColor
            )
        }
        for (i in 0 until size) {
            addLine(i, i + size, connectorColor)
        }
    }

    /**
     * Recomputes this geometry's transform matrix.
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

    /**
     * Invoke [f] for each position and the color it's associated with.
     */
    inline fun forEachVertex(f: (position: Vector4dh, color: Color) -> Unit) {
        lines.forEach {
            f(it.start, it.color)
            f(it.end, it.color)
        }
    }

    /**
     * Project geometry down to a 3d wireframe.
     * @param f Called for each wireframe vertex, i.e. its position and color.
     */
    inline fun generateProjectedWireframe(f: (position: Vector3dh, color: Color) -> Unit) {
        computeModelMatrix()

        val transformedPosition = Vector4dh()
        val homogeneous = Vector3dh()

        lines.forEach { line ->
            line.forEachPosition { position ->

                // Apply 4-dimensional model matrix to 4d point:
                transformedPosition.multiplication(position, modelMatrix)

                // Project vector down to a 3d volume:
                transformedPosition /= transformedPosition.q + Q_PROJECTION_VOLUME

                homogeneous.load(transformedPosition.x, transformedPosition.y, transformedPosition.z)

                f(homogeneous, line.color)
            }
        }
    }

}
