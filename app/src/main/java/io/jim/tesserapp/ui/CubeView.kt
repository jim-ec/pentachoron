package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * A coordinate system view featuring a cube.
 */
class CubeView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    val graphicsView = GraphicsView(context, null)

    init {
        Geometry("Cube", Color(context, R.color.colorAccent)).apply {
            addQuadrilateral(
                    Vector4dh(1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, -1.0, 1.0, 0.0),
                    Vector4dh(1.0, -1.0, 1.0, 0.0)
            )

            extrude(Vector4dh(0.0, 0.0, -2.0, 0.0))

            graphicsView.sharedRenderData.drawDataProvider += this
            graphicsView.sharedRenderData.controlledGeometry = this
        }

        addView(graphicsView)
    }

}
