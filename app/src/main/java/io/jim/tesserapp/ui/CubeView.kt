package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.ui.controllers.Controllable
import io.jim.tesserapp.ui.controllers.Rotatable
import io.jim.tesserapp.ui.controllers.Translatable
import kotlin.properties.Delegates

/**
 * A coordinate system view featuring a cube.
 */
class CubeView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Controls the cube.
     */
    val cubeController = object : Controllable {

        override fun setup(view: ControllerView) {
            view.controlCamera(coordinateSystemView.sharedRenderData)
        }

        override val rotation = object : Rotatable {

            /**
             * Rotate the cube around the x-axis.
             */
            override var x = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothRotation.x = value
                    }
                }

            /**
             * Rotate the cube around the y-axis.
             */
            override var y = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothRotation.y = value
                    }
                }

            /**
             * Rotate the cube around the z-axis.
             */
            override var z = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothRotation.z = value
                    }
                }

            /**
             *  Rotate the cube around on the q-x plane.
             */
            override var q = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        //cube.rotation.q = value
                    }
                }

        }

        override val translation = object : Translatable {

            /**
             * Translates the cube along the x-axis.
             */
            override var x = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothTranslation.x = value
                    }
                }

            /**
             * Translates the cube along the y-axis.
             */
            override var y = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothTranslation.y = value
                    }
                }

            /**
             * Translates the cube along the z-axis.
             */
            override var z = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothTranslation.z = value
                    }
                }

            /**
             * Translates the cube along the q-axis.
             */
            override var q = 0f
                set(value) {
                    coordinateSystemView.sharedRenderData.synchronized {
                        cube.smoothTranslation.q = value
                    }
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

    private val coordinateSystemView = GraphicsView(context, null)

    /**
     * The featuring cube.
     */
    private val cube = Quadrilateral("Cube",
            Vector3d(1f, 1f, 1f),
            Vector3d(-1f, 1f, 1f),
            Vector3d(-1f, -1f, 1f),
            Vector3d(1f, -1f, 1f),
            Color(context, R.color.colorAccent)
    ).apply {
        coordinateSystemView.sharedRenderData.synchronized { renderData ->
            renderData.drawDataProvider += this
            extrude(Vector3d(0f, 0f, -2f))
        }
    }

    init {
        addView(coordinateSystemView)
    }

}
