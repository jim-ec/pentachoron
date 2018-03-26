package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import io.jim.tesserapp.geometry.Line
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.geometry.Triangle
import io.jim.tesserapp.math.Vector

/**
 * A view capable of rendering 3D geometry.
 */
class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(100)

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

}
