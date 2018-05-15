package io.jim.tesserapp.ui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Axis
import io.jim.tesserapp.geometry.Grid
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.common.Pi
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.rendering.Renderer

/**
 * A view capable of rendering 3D geometry.
 */
class CoordinateSystemView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(context, resources.displayMetrics.xdpi)

    /**
     * Render data shared across render and other threads.
     */
    val sharedRenderData = renderer.sharedRenderData

    private val touchStartPosition = Vector3d(0f, 0f, 0f)
    private var touchStartTime = 0L

    private val grid = Grid(color = Color(context, R.color.colorGrid))

    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.02f
        const val DEFAULT_CAMERA_DISTANCE = 8f
        const val DEFAULT_CAMERA_HORIZONTAL_ROTATION = -Pi / 8f
        const val DEFAULT_CAMERA_VERTICAL_ROTATION = Pi / 3f
    }

    init {

        // Setup renderer:
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY

        // Create axis:
        sharedRenderData.geometryManager += Axis(
                xAxisColor = Color(context, R.color.colorAxisX),
                yAxisColor = Color(context, R.color.colorAxisY),
                zAxisColor = Color(context, R.color.colorAxisZ)
        )

        // Create grid:
        enableGrid(true)

        // Default camera rotation:
        moveToDefaultCameraPosition()
    }

    private fun moveToDefaultCameraPosition() {
        sharedRenderData.synchronized { renderData ->
            renderData.camera.distance = DEFAULT_CAMERA_DISTANCE
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
                    touchStartPosition.x = event.x
                    touchStartPosition.y = event.y
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

                    touchStartPosition.x = event.x
                    touchStartPosition.y = event.y
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

    /**
     * Enable or disable grid rendering.
     */
    fun enableGrid(enable: Boolean) {
        renderer.sharedRenderData.synchronized { renderData ->
            if (enable) {
                renderData.geometryManager += grid
            }
            else {
                renderData.geometryManager -= grid
            }
        }
    }

}
