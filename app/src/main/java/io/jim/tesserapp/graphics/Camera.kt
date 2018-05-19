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
     * Distance of camera position from center.
     */
    var distance by Smoothed<Camera>(
            startValue = 0.0,
            transitionInterval = 300L
    )

    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    var horizontalRotation by Smoothed<Camera>(
            startValue = 0.0,
            transitionInterval = 80L
    )

    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    var verticalRotation by Smoothed<Camera>(
            startValue = 0.0,
            transitionInterval = 80L
    )

}
