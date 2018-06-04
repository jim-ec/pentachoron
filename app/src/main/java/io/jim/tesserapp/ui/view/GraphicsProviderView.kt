package io.jim.tesserapp.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * A view providing a graphics view instance.
 */
abstract class GraphicsProviderView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * The [GraphicsView] hold by this provider.
     */
    abstract val graphicsView: GraphicsView

}
