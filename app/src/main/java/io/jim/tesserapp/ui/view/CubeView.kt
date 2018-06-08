package io.jim.tesserapp.ui.view

import android.content.Context
import android.util.AttributeSet
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * A coordinate system view featuring a cube.
 */
class CubeView : GraphicsProviderView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override val graphicsView = GraphicsView(context, null)

    init {
        (context as MainActivity).viewModel.featuredGeometry.apply {
            erase()

            addQuadrilateral(
                    Vector4dh(1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, 1.0, 1.0, 0.0),
                    Vector4dh(-1.0, -1.0, 1.0, 0.0),
                    Vector4dh(1.0, -1.0, 1.0, 0.0)
            )

            extrude(Vector4dh(0.0, 0.0, -2.0, 0.0))
        }

        addView(graphicsView)
    }

}
