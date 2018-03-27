package io.jim.tesserapp.gui

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import io.jim.tesserapp.geometry.Line
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.geometry.Triangle
import io.jim.tesserapp.math.Vector

/**
 * A view capable of rendering 3D geometry.
 */
class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(100)
    private val touchStartPosition = Vector(2)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_WHEN_DIRTY

        renderer.addGeometry(Triangle(Vector.point(0.0, 1.0, 0.5), Vector.point(1.0, -1.0, 0.5), Vector.point(-1.0, -1.0, 0.5), Color.GREEN))
        renderer.addGeometry(Line(Vector.point(1.0, 1.0, 0.7), Vector.point(-1.0, -1.0, 0.7), Color.RED))
        renderer.addGeometry(Quadrilateral(
                Vector.point(1.0, 1.0, 0.5),
                Vector.point(-1.0, 1.0, 0.5),
                Vector.point(-1.0, -1.0, 0.5),
                Vector.point(1.0, -1.0, 0.5),
                Color.BLUE
        ))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null == event) return false

        if (event.action == ACTION_DOWN) {
            touchStartPosition.x = event.x.toDouble()
            touchStartPosition.y = event.y.toDouble()
            return true
        }

        if (event.action == ACTION_MOVE) {
            val dx = event.x.toDouble() - touchStartPosition.x
            renderer.rotate(0.0, dx * 0.0001)
            requestRender()
            return true
        }

        return false
    }

}
