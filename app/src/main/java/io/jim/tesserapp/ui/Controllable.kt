package io.jim.tesserapp.ui

import io.jim.tesserapp.math.Rotatable
import io.jim.tesserapp.math.Translatable

/**
 * The set of GUI-controls set values through this interface.
 */
interface Controllable {

    /**
     * Rotation transform.
     */
    val rotation: Rotatable

    /**
     * Translation transform.
     */
    val translation: Translatable

    /**
     * Camera distance.
     */
    var cameraDistance: Float

    /**
     * Option whether to render the base-grid.
     */
    var renderGrid: Boolean

}
