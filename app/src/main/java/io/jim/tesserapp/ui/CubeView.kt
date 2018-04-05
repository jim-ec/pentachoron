package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A coordinate system view featuring a cube.
 */
class CubeView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    /**
     * The underlying coordinate system view.
     */
    val coordinateSystemView = CoordinateSystemView(context, null)

    /**
     * The featuring cube.
     */
    val cube = Quadrilateral("Cube", Vector(1f, 1f, 1f, 1f),
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
