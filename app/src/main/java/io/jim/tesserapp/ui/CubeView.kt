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
    val cubeController = object : Controllable {

        /**
         * Rotate the cube around the x-axis.
         */
        override var rotationX: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.rotation.x = newValue
            }
        }

        /**
         * Rotate the cube around the y-axis.
         */
        override var rotationY: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.rotation.y = newValue
            }
        }

        /**
         * Rotate the cube around on the z-axis.
         */
        override var rotationZ: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.rotation.z = newValue
            }
        }

        /**
         * Rotate the cube around on the q-x plane.
         */
        override var rotationQ: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.rotation.q = newValue
            }
        }

        /**
         * Translates the cube along the x-axis.
         */
        override var translationX: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.translation.x = newValue
            }
        }

        /**
         * Translates the cube along the y-axis.
         */
        override var translationY: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.translation.y = newValue
            }
        }

        /**
         * Translates the cube along the z-axis.
         */
        override var translationZ: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.translation.z = newValue
            }
        }

        /**
         * Translates the cube along the q-axis.
         */
        override var translationQ: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                cube.translation.q = newValue
            }
        }

        /**
         * Control the camera distance.
         */
        override var cameraDistance: Float by Delegates.observable(0f) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                coordinateSystemView.sharedRenderData.camera.distance = newValue
            }
        }

        /**
         * Render grid option.
         */
        override var renderGrid: Boolean by Delegates.observable(true) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                coordinateSystemView.enableGrid(newValue)
            }
        }
    }

    private val coordinateSystemView = CoordinateSystemView(context, null)

    /**
     * The featuring cube.
     */
    private val cube = Quadrilateral("Cube",
            Vector(1f, 1f, 1f, 1f),
            Vector(-1f, 1f, 1f, 1f),
            Vector(-1f, -1f, 1f, 1f),
            Vector(1f, -1f, 1f, 1f),
            Color(context, R.color.colorAccent)
    ).apply {
        coordinateSystemView.sharedRenderData.synchronized { renderData ->
            renderData.geometryManager += this
            extrude(Vector(0f, 0f, -2f, 0f))
        }
    }

    init {
        addView(coordinateSystemView)
    }

}
