package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.vector.VectorN

/**
 *
 */
class Transform {
    
    var rotationMatrix = Matrix(5)
    
    val oldRotationMatrix = Matrix(5)
    
    /**
     * Absolute translation of this geometry.
     */
    val translation = VectorN(4)
    
    /**
     * Used in [computeModelMatrix] to express [translation] in a matrix,
     * so it can be multiplied with [rotationMatrix] to final [modelMatrix].
     */
    private val translationMatrix = Matrix(5)
    
    /**
     * This geometries model matrix.
     * [computeModelMatrix] must be called in order to keep the model matrix up-to-date.
     */
    var modelMatrix = Matrix(5)
    
    /**
     * Rotate the geometry around the *global* x-axis, i.e. the yz-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     */
    fun rotateX(deltaAngle: Double) {
        applyNewRotation(Matrix(5).apply { rotation(a = 1, b = 2, radians = deltaAngle) })
    }
    
    /**
     * Rotate the geometry around the *global* y-axis, i.e. the zx-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     */
    fun rotateY(deltaAngle: Double) {
        applyNewRotation(Matrix(5).apply { rotation(a = 2, b = 0, radians = deltaAngle) })
    }
    
    /**
     * Rotate the geometry around the *global* z-axis, i.e. the xy-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     */
    fun rotateZ(deltaAngle: Double) {
        applyNewRotation(Matrix(5).apply { rotation(a = 0, b = 1, radians = deltaAngle) })
    }
    
    /**
     * Rotate the geometry in the *global* the xq-plane.
     * The set rotation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAngle Amount of rotation, in radians.
     */
    fun rotateQ(deltaAngle: Double) {
        applyNewRotation(Matrix(5).apply { rotation(a = 0, b = 3, radians = deltaAngle) })
    }
    
    /**
     * Apply rotation described in [newRotationMatrix] to [rotationMatrix].
     */
    private fun applyNewRotation(newRotationMatrix: Matrix) {
        
        // Move contents of model matrix into the temporary buffer-like old-model-matrix:
        oldRotationMatrix.swap(rotationMatrix)
    
        rotationMatrix = newRotationMatrix * oldRotationMatrix
    }
    
    /**
     * Translate the geometry along the x-axis.
     * The set translation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAmount Amount of translation.
     */
    fun translateX(deltaAmount: Double) {
        translation.x += deltaAmount
    }
    
    /**
     * Translate the geometry along the y-axis.
     * The set translation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAmount Amount of translation.
     */
    fun translateY(deltaAmount: Double) {
        translation.y += deltaAmount
    }
    
    /**
     * Translate the geometry along the z-axis.
     * The set translation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAmount Amount of translation.
     */
    fun translateZ(deltaAmount: Double) {
        translation.z += deltaAmount
    }
    
    /**
     * Translate the geometry along the q-axis.
     * The set translation is in no ways absolute, but rather accumulated to the current transform.
     *
     * @param deltaAmount Amount of translation.
     */
    fun translateQ(deltaAmount: Double) {
        translation.q += deltaAmount
    }
    
    /**
     * Recomputes this geometry's transform matrix.
     */
    fun computeModelMatrix() {
        translationMatrix.translation(translation)
        modelMatrix = rotationMatrix * translationMatrix
    }
    
}
