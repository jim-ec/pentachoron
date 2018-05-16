package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.common.SmoothTimedValueDelegate

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
    var distance by SmoothTimedValueDelegate<Camera>(
            startValue = 0f,
            transitionTimeInterval = 300L
    )

    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    var horizontalRotation by SmoothTimedValueDelegate<Camera>(
            startValue = 0f,
            transitionTimeInterval = 80L
    )

    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    var verticalRotation by SmoothTimedValueDelegate<Camera>(
            startValue = 0f,
            transitionTimeInterval = 80L
    )

}
