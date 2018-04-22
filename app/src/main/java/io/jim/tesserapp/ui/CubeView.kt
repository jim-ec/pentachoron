package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import kotlin.properties.Delegates

/**
 * A coordinate system view featuring a cube.
 */
class CubeView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    /**
     * Controls the cube.
     */
    val cubeController = object : ControllerView.Controllable {

        /**
         * Rotates the cube around the y-axis.
         */
        override var transformRotationXZ: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.rotationZX(newValue)
            }
        }

        /**
         * Rotates the cube around the z-axis.
         */
        override var transformRotationXY: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.rotationYX(newValue)
            }
        }

        /**
         * Translates the cube along the x-axis.
         */
        override var transformTranslationX: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.translate(Vector(newValue, 0f, 0f, 1f))
            }
        }

        /**
         * Control the camera distance.
         */
        override var transformCameraDistance: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                coordinateSystemView.sharedRenderData.cameraDistance = newValue
            }
        }

        /**
         * Render grid option.
         */
        override var renderGrid: Boolean by Delegates.observable(true) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                coordinateSystemView.enableGrid(newValue)
            }
        }
    }

    private val coordinateSystemView = CoordinateSystemView(context, null)

    /**
     * The featuring cube.
     */
    private val cube = Quadrilateral("Cube", Vector(1f, 1f, 1f, 1f),
            Vector(-1f, 1f, 1f, 1f),
            Vector(-1f, -1f, 1f, 1f),
            Vector(1f, -1f, 1f, 1f),
            Color(context, R.color.colorAccent)
    ).apply {
        synchronized(coordinateSystemView.sharedRenderData) {
            addToParentGeometry(coordinateSystemView.sharedRenderData.rootGeometry)
            extrude(Vector(0f, 0f, -2f, 0f))
        }
    }

    init {
        addView(coordinateSystemView)
    }

}
