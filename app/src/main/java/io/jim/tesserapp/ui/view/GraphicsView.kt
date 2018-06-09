package io.jim.tesserapp.ui.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.axis
import io.jim.tesserapp.geometry.grid
import io.jim.tesserapp.graphics.themedColorInt
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.rendering.Renderer

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    val viewModel = (context as MainActivity).viewModel

    private val renderer = Renderer(
            context as MainActivity,
            resources.displayMetrics.xdpi.toDouble()
    )

    private val touchStartPosition = Vector3d(0.0, 0.0, 0.0)
    private var touchStartTime = 0L

    private val grid =
            Geometry().apply {
                name = "Grid"
                baseColor = themedColorInt(context, R.attr.colorGrid)
                grid()
            }

    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.02
    }

    /**
     * Enable or disable grid rendering.
     */
    var renderGrid = true
        set(value) {
            if (value) {
                renderer.addGeometry(grid)
            }
            else {
                renderer.removeGeometry(grid)
            }
            field = value
        }

    init {

        // Setup renderer:
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY

        // Create axis:
        renderer.addGeometry(Geometry().apply {
            name = "Axis"
            axis(
                    xAxisColor = themedColorInt(context, R.attr.colorAxisX),
                    yAxisColor = themedColorInt(context, R.attr.colorAxisY),
                    zAxisColor = themedColorInt(context, R.attr.colorAxisZ)
            )
        })
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
