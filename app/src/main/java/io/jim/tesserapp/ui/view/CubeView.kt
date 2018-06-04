package io.jim.tesserapp.ui.view

import android.content.Context
import android.util.AttributeSet
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * A coordinate system view featuring a cube.
 */
class CubeView : GraphicsProviderView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override val graphicsView = GraphicsView(context, null)

    init {
        Geometry("Cube", Color(context, R.color.colorAccent)).also { cube ->
            cube.addQuadrilateral(
                    Vector4dh(1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, -1.0, 1.0, 0.0),
                    Vector4dh(1.0, -1.0, 1.0, 0.0)
            )

            cube.extrude(Vector4dh(0.0, 0.0, -2.0, 0.0))

            graphicsView.sharedRenderData.drawDataProvider += cube
            graphicsView.sharedRenderData.controlledGeometry = cube
        }

        addView(graphicsView)
    }

}
