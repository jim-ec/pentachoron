package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import io.jim.tesserapp.math.Vector

class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(3)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_WHEN_DIRTY

        //renderer.appendLine(Pair(Vector(0.0, 100.0), Vector(100.0, 100.0)), Color.RED)
        //renderer.appendLine(Pair(Vector(-100.0, 100.0), Vector(100.0, 100.0)), Color.BLUE)

        Handler().postDelayed({
            renderer.appendTriangle(Vector(0.0, 1.0, 1.0), Vector(1.0, -1.0, 1.0), Vector(-1.0, -1.0, 1.0), Color.GREEN)
            requestRender()
        }, 1000)
    }

}
