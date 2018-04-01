package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.geometry.Line
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Point

/**
 * A view capable of rendering 3D geometry.
 */
class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(100)
    private val touchStartPosition = Point(0.0, 0.0)
    private val rotation = Point(0.0, 0.0)
    private var touchStartTime = 0L

    companion object {

        const val CLICK_TIME_MS = 100L

    }

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_WHEN_DIRTY

        renderer.addGeometry(Quadrilateral(
                Point(1.0, 1.0, 1.0),
                Point(-1.0, 1.0, 1.0),
                Point(-1.0, -1.0, 1.0),
                Point(1.0, -1.0, 1.0),
                Color.BLACK
        ).apply { extrude(Direction(0.0, 0.0, -2.0)) })

        renderer.addGeometry(Line(Point(0.0, 0.0, 0.0), Point(1.0, 0.0, 0.0), Color.RED))
        renderer.addGeometry(Line(Point(0.0, 0.0, 0.0), Point(0.0, 1.0, 0.0), Color.GREEN))
        renderer.addGeometry(Line(Point(0.0, 0.0, 0.0), Point(0.0, 0.0, 1.0), Color.BLUE))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null == event) return false
        val x = event.x.toDouble()
        val y = event.y.toDouble()

        if (event.action == ACTION_DOWN) {
            touchStartPosition.x = x
            touchStartPosition.y = y
            touchStartTime = System.currentTimeMillis()
            return true
        }

        if (event.action == ACTION_MOVE) {
            val dx = x - touchStartPosition.x
            val dy = y - touchStartPosition.y
            rotation.x += dx
            rotation.y += dy
            renderer.rotation(rotation.x * 0.005, rotation.y * 0.005)
            requestRender()

            touchStartPosition.x = x
            touchStartPosition.y = y
            return true
        }

        if (event.action == ACTION_UP && System.currentTimeMillis() - touchStartTime < CLICK_TIME_MS) {
            performClick()
        }

        return false
    }

    override fun performClick(): Boolean {
        super.performClick()

        renderer.rotation(0.0, 0.0)
        requestRender()

        return true
    }

}
