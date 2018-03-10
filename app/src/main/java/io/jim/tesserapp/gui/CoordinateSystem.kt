package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import io.jim.tesserapp.math.Vector

class CoordinateSystem(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private val renderer = Renderer(3, 2)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_WHEN_DIRTY

        Handler().postDelayed({
            renderer.appendTriangle(Vector(0.0, 1.0, 0.7), Vector(1.0, -1.0, 0.7), Vector(-1.0, -1.0, 0.7), Color.GREEN)
            renderer.appendLine(Vector(-1.0, 0.0, 0.8), Vector(1.0, 0.0, 0.8), Color.RED)
            requestRender()
        }, 1000)
    }

}
