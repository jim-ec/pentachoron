package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.common.Smoothed

/**
 * Encapsulate camera parameters.
 */
class Camera {

    /**
     * Aspect ratio.
     */
    var aspectRatio: Float = 1f

    /**
     * Distance of camera position from center.
     */
    var distance by Smoothed<Camera>(
            startValue = 0f,
            transitionInterval = 300L
    )

    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    var horizontalRotation by Smoothed<Camera>(
            startValue = 0f,
            transitionInterval = 80L
    )

    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    var verticalRotation by Smoothed<Camera>(
            startValue = 0f,
            transitionInterval = 80L
    )

}
