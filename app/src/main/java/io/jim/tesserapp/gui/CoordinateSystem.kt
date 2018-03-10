package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import io.jim.tesserapp.math.Vector

class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(3)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY

        renderer.clearVertices()
        //renderer.appendLine(Pair(Vector(0.0, 100.0), Vector(100.0, 100.0)), Color.RED)
        //renderer.appendLine(Pair(Vector(-100.0, 100.0), Vector(100.0, 100.0)), Color.BLUE)
        renderer.appendTriangle(Vector(0.0, 1.0, 1.0), Vector(1.0, -1.0, 1.0), Vector(-1.0, -1.0, 1.0), Color.GREEN)
    }

}
