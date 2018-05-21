package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.vector.Vector3dh
import io.jim.tesserapp.math.vector.Vector4dh

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
     * Describes the way new transform, i.e. [currentTransformMatrix], is applied,
     * say *merged* into previous, already existent transform, i.e. [modelMatrix].
     */
    enum class TransformApplyMode {

        /**
         * New transform is prepended to previous transform,
         * i.e. previous transform is parented to new transform.
         */
        PREPEND,

        /**
         * New transform is appended to previous transform,
         * i.e. new transform is parented to previous transform.
         */
        APPEND
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

    /**
     * When transforming the geometry, the transform is firstly expressed into this matrix.
     * Afterwards, a transform applier function will merge this [currentTransformMatrix] into
     * the already existent [modelMatrix], whose content is not discarded, resulting
     * into a combined transform.
     */
    private val currentTransformMatrix = Matrix(5)

    /**
     * Since no matrix cannot be simultaneously target and source of the same multiplication
     * operation, the [modelMatrix] must be copied into this [oldModelMatrix],
     * which is then multiplied with [currentTransformMatrix] and stored again in [modelMatrix].
     */
    private val oldModelMatrix = Matrix(5)

    /**
     * Rotate the geometry around the x-axis, i.e. the yz-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     *
     * @param mode How this new transform will be applied to already existent transform.
     * - [TransformApplyMode.PREPEND]: Rotation stays *aligned* with the global x-axis.
     * - [TransformApplyMode.APPEND]: Rotates locally around the current geometry's x-axis.
     */
    fun rotateX(deltaAngle: Double, mode: TransformApplyMode) {
        currentTransformMatrix.identity()
        currentTransformMatrix.rotation(a = 1, b = 2, radians = deltaAngle)

        applyCurrentTransform(mode)
    }

    /**
     * Rotate the geometry around the y-axis, i.e. the zx-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     *
     * @param mode How this new transform will be applied to already existent transform.
     * - [TransformApplyMode.PREPEND]: Rotation stays *aligned* with the global y-axis.
     * - [TransformApplyMode.APPEND]: Rotates locally around the current geometry's y-axis.
     */
    fun rotateY(deltaAngle: Double, mode: TransformApplyMode) {
        currentTransformMatrix.identity()
        currentTransformMatrix.rotation(a = 2, b = 0, radians = deltaAngle)

        applyCurrentTransform(mode)
    }

    /**
     * Rotate the geometry around the z-axis, i.e. the xy-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     *
     * @param mode How this new transform will be applied to already existent transform.
     * - [TransformApplyMode.PREPEND]: Rotation stays *aligned* with the global z-axis.
     * - [TransformApplyMode.APPEND]: Rotates locally around the current geometry's z-axis.
     */
    fun rotateZ(deltaAngle: Double, mode: TransformApplyMode) {
        currentTransformMatrix.identity()
        currentTransformMatrix.rotation(a = 0, b = 1, radians = deltaAngle)

        applyCurrentTransform(mode)
    }

    /**
     * Translate the geometry along the x-axis.
     * The set translation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAmount Amount of translation.
     *
     * @param mode How this new transform will be applied to already existent transform.
     * - [TransformApplyMode.PREPEND]: Translation stays *aligned* with the global x-axis.
     * - [TransformApplyMode.APPEND]: Translates locally along the current geometry's x-axis.
     */
    fun translateX(deltaAmount: Double, mode: TransformApplyMode) {
        currentTransformMatrix.identity()
        currentTransformMatrix.translation(0, deltaAmount)

        applyCurrentTransform(mode)
    }

    /**
     * Apply transform described in [currentTransformMatrix] to [modelMatrix].
     *
     * @param mode
     * Determines whether the transform is either prepended (use [TransformApplyMode.PREPEND])
     * or appended (use [TransformApplyMode.APPEND]).
     */
    private fun applyCurrentTransform(mode: TransformApplyMode) {

        // Move contents of model matrix into the temporary buffer-like old-model-matrix:
        oldModelMatrix.swap(modelMatrix)

        when (mode) {
            TransformApplyMode.PREPEND ->
                // Pre-multiply previous transform to new transform:
                modelMatrix.multiplication(oldModelMatrix, currentTransformMatrix)

            TransformApplyMode.APPEND ->
                // Post-multiply previous transform to new transform:
                modelMatrix.multiplication(currentTransformMatrix, oldModelMatrix)
        }
    }

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
