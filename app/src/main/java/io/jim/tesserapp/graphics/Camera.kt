package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.common.Smoothed

/**
 * Encapsulate camera parameters.
 */
class Camera {

    /**
     * Aspect ratio.
     */
    var aspectRatio: Double = 1.0

    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    var horizontalRotation by Smoothed(
            startValue = 0.0,
            transitionInterval = 80.0
    )

    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    var verticalRotation by Smoothed(
            startValue = 0.0,
            transitionInterval = 80.0
    )

}
