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
         * Rotates the cube around the x-axis.
         */
        override var rotationX: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.rotation.x = newValue
            }
        }

        /**
         * Rotates the cube around the y-axis.
         */
        override var rotationY: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.rotation.y = newValue
            }
        }

        /**
         * Rotates the cube around the z-axis.
         */
        override var rotationZ: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.rotation.z = newValue
            }
        }

        /**
         * Translates the cube along the x-axis.
         */
        override var translationX: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.translation.x = newValue
            }
        }

        /**
         * Translates the cube along the y-axis.
         */
        override var translationY: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.translation.y = newValue
            }
        }

        /**
         * Translates the cube along the z-axis.
         */
        override var translationZ: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.translation.z = newValue
            }
        }

        /**
         * Translates the cube along the w-axis.
         */
        override var translationW: Float by Delegates.observable(0f) { _, _, newValue ->
            synchronized(coordinateSystemView.sharedRenderData) {
                cube.translation.w = newValue
            }
        }

        /**
         * Control the camera distance.
         */
        override var cameraDistance: Float by Delegates.observable(0f) { _, _, newValue ->
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
