package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Line
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.math.Vector

/**
 * A view capable of rendering 3D geometry.
 */
class CoordinateSystemView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(100, context)
    private val touchStartPosition = Vector(0.0, 0.0)
    private val rotation = Vector(0.0, 0.0)
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
                Vector(1.0, 1.0, 1.0),
                Vector(-1.0, 1.0, 1.0),
                Vector(-1.0, -1.0, 1.0),
                Vector(1.0, -1.0, 1.0),
                Color(context, R.color.colorAccent)
        ).apply { extrude(Vector(0.0, 0.0, -2.0)) })

        renderer.addGeometry(Line(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), Color(context, R.color.colorPrimary)))
        renderer.addGeometry(Line(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), Color(context, R.color.colorPrimary)))
        renderer.addGeometry(Line(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), Color(context, R.color.colorPrimary)))

        for (i in -5..5) {
            renderer.addGeometry(Line(
                    Vector(i.toDouble(), 0.0, -5.0),
                    Vector(i.toDouble(), 0.0, 5.0),
                    Color(context, R.color.colorGrid)
            ))
            renderer.addGeometry(Line(
                    Vector(-5.0, 0.0, i.toDouble()),
                    Vector(5.0, 0.0, i.toDouble()),
                    Color(context, R.color.colorGrid)
            ))
        }
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
            rotation.x = 0.0
            rotation.y = 0.0
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
