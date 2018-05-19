package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.jim.tesserapp.R

/**
 * A container for both the graphics view and the controller view.
 *
 * References to both must be specified in XML:
 *
 *  - `app:graphicsViewProvider`:
 *  Reference to a descendant XML element, subclassing from [GraphicsProviderView].
 *
 *  - `app:controller`:
 *  Reference to a descendant XML element, subclassing from [ControllerView].
 *
 *  The controller view is instructed to control the graphics view retrieved by the graphics view
 *  provider, using [ControllerView.control].
 */
class ControlledGraphicsContainerView(context: Context, attrs: AttributeSet?)
    : FrameLayout(context, attrs) {

    private val graphicsViewProviderId: Int
    private val controllerViewId: Int

    init {
        val attributes = context.obtainStyledAttributes(
                attrs, R.styleable.ControlledGraphicsContainerView)

        graphicsViewProviderId = attributes.getResourceId(
                R.styleable.ControlledGraphicsContainerView_graphicsViewProvider, 0)

        controllerViewId = attributes.getResourceId(
                R.styleable.ControlledGraphicsContainerView_controller, 0)

        attributes.recycle()
    }

    /**
     * After inflating has finished, the actual view objects are retrieved and
     * the controller view is connected to the graphics view.
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        // Find the controller view ...
        (findViewById<ControllerView>(controllerViewId)
                ?: throw RuntimeException("Cannot find controller view"))

                // ... and link it to the graphics view provided by the graphics view provider:
                .control(
                        (findViewById<GraphicsProviderView>(graphicsViewProviderId)
                                ?: throw RuntimeException("Cannot find graphics view provider"))
                                .graphicsView
                )
    }

}
