package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import io.jim.tesserapp.math.Vector

class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(1)

    init {
        renderer.clearVertices()
        //renderer.appendLine(Pair(Vector(0.0, 100.0), Vector(100.0, 100.0)), Color.RED)
        //renderer.appendLine(Pair(Vector(-100.0, 100.0), Vector(100.0, 100.0)), Color.BLUE)
        renderer.appendTriangle(Vector(0.0, 1.0, 1.0), Vector(1.0, -1.0, 1.0), Vector(-1.0, -1.0, 1.0), Color.GREEN)

        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private fun transform(v: Vector): Vector {
        return Vector(v.x, -v.y) * 100.0 + Vector(width / 2.0, height / 2.0)
    }

    private fun tx(v: Vector) = transform(v).x.toFloat()
    private fun ty(v: Vector) = transform(v).y.toFloat()

}
