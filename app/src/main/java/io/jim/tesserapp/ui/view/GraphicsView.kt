package io.jim.tesserapp.ui.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.axis
import io.jim.tesserapp.geometry.grid
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.rendering.Renderer

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val renderer = Renderer(context, resources.displayMetrics.xdpi.toDouble())

    /**
     * Render data shared across render and other threads.
     */
    val sharedRenderData = renderer.sharedRenderData

    private val touchStartPosition = Vector3d(0.0, 0.0, 0.0)
    private var touchStartTime = 0L

    private val grid =
            Geometry(
                    "Grid",
                    Color(context, R.style.AppTheme, android.R.attr.textColorPrimary)
                            .apply { luminance(0.6) }
            ).apply {
                grid()
            }

    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.02
        private const val DEFAULT_CAMERA_HORIZONTAL_ROTATION = -Math.PI / 8.0
        private const val DEFAULT_CAMERA_VERTICAL_ROTATION = Math.PI / 3.0
    }

    /**
     * Enable or disable grid rendering.
     */
    var renderGrid = true
        set(value) {
            sharedRenderData.synchronized { (drawDataProvider) ->
                if (value) {
                    drawDataProvider += grid
                }
                else {
                    drawDataProvider -= grid
                }
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
        sharedRenderData.drawDataProvider += Geometry("Axis").apply {
            axis(
                    xAxisColor = Color(context, R.color.axisX),
                    yAxisColor = Color(context, R.color.axisY),
                    zAxisColor = Color(context, R.color.axisZ)
            )
        }

        // Default camera rotation:
        moveToDefaultCameraPosition()
    }

    private fun moveToDefaultCameraPosition() {
        sharedRenderData.synchronized { renderData ->
            renderData.camera.verticalRotation = DEFAULT_CAMERA_HORIZONTAL_ROTATION
            renderData.camera.horizontalRotation = DEFAULT_CAMERA_VERTICAL_ROTATION
        }
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

                    renderer.sharedRenderData.synchronized { renderData ->
                        renderData.camera.horizontalRotation += dx * TOUCH_ROTATION_SENSITIVITY
                        renderData.camera.verticalRotation -= dy * TOUCH_ROTATION_SENSITIVITY
                    }

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
        moveToDefaultCameraPosition()
        return true
    }

}
