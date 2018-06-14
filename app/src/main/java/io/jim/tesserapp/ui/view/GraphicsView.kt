package io.jim.tesserapp.ui.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.graphics.themedColorInt
import io.jim.tesserapp.math.vector.Vector3d

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val viewModel = (context as MainActivity).viewModel

    @PublishedApi
    internal val renderer = Renderer(
            themedColorInt(context, android.R.attr.windowBackground),
            viewModel,
            context.assets,
            resources.displayMetrics.xdpi.toDouble()
    )

    private val touchStartPosition = Vector3d(0.0, 0.0, 0.0)
    private var touchStartTime = 0L

    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.008
    }

    init {

        // Setup renderer:
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    /**
     * Handles camera orbit position upon touch events.
     */
    override fun onTouchEvent(event: MotionEvent?) =
            if (null == event) false
            else when {
                event.action == ACTION_DOWN -> {
                    touchStartPosition.x = event.x.toDouble()
                    touchStartPosition.y = event.y.toDouble()
                    touchStartTime = System.currentTimeMillis()
                    true
                }
                event.action == ACTION_MOVE -> {
                    val dx = event.x - touchStartPosition.x
                    val dy = event.y - touchStartPosition.y

                    viewModel.horizontalCameraRotation.value += dx * TOUCH_ROTATION_SENSITIVITY
                    viewModel.verticalCameraRotation.value -= dy * TOUCH_ROTATION_SENSITIVITY

                    touchStartPosition.x = event.x.toDouble()
                    touchStartPosition.y = event.y.toDouble()
                    true
                }
                event.action == ACTION_UP
                        && System.currentTimeMillis() - touchStartTime < CLICK_TIME_MS -> {
                    performClick()
                    true
                }
                else -> false
            }

    /**
     * Clicks rewind camera position to default.
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

}
