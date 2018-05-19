package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh
import io.jim.tesserapp.ui.controllers.Controllable
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

        /**
         * Render grid option.
         */
        override var renderGrid: Boolean by Delegates.observable(true) { _, _, newValue ->
            coordinateSystemView.sharedRenderData.synchronized {
                coordinateSystemView.enableGrid(newValue)
            }
        }
    }

    val coordinateSystemView = GraphicsView(context, null)

    init {
        Geometry("Cube", Color(context, R.color.colorAccent)).apply {
            addQuadrilateral(
                    Vector4dh(1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, -1.0, 1.0, 0.0),
                    Vector4dh(1.0, -1.0, 1.0, 0.0)
            )

            extrude(Vector4dh(0.0, 0.0, -2.0, 0.0))

            coordinateSystemView.sharedRenderData.drawDataProvider += this
            coordinateSystemView.sharedRenderData.controlledGeometry = this
        }

        addView(coordinateSystemView)
    }

}
