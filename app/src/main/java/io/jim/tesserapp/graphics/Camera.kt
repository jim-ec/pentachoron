package io.jim.tesserapp.graphics

data class Camera(

        /**
         * Distance of camera position from center.
         */
        var distance: Float = 1f,

        /**
         * Aspect ratio.
         */
        var aspectRatio: Float = 1f,

        /**
         * Rotation on the horizontal orbit.
         * This is the base rotation.
         */
        var horizontalRotation: Float = 0f,

        /**
         * Rotation on the vertical orbit.
         * This is the secondary rotation.
         */
        var verticalRotation: Float = 0f

)
