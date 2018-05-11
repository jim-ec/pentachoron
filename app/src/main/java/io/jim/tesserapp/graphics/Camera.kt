package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.common.SmoothTimedValueDelegate

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
    var horizontalRotation = 0f

    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    var verticalRotation by SmoothTimedValueDelegate<Camera>(
            startValue = 0f,
            transitionTimeInterval = 80L
    )

}
