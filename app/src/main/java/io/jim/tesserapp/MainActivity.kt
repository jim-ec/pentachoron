package io.jim.tesserapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.ui.ControllerView
import io.jim.tesserapp.ui.CoordinateSystemView

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var coordinateSystemView: CoordinateSystemView
    private lateinit var controllerView: ControllerView

    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coordinateSystemView = findViewById(R.id.coordinateSystemView)
        controllerView = findViewById(R.id.controllerView)


        controllerView.cameraControlDistance.listeners += fun(distance: Float) {
            coordinateSystemView.renderer.cameraDistance = distance
            coordinateSystemView.requestRender()
        }

        controllerView.rotationControlXZ.listeners += fun(rotation: Float) {
            coordinateSystemView.cube.rotationZX(rotation)
        }

        controllerView.rotationControlXY.listeners += fun(rotation: Float) {
            coordinateSystemView.cube.rotationYX(rotation)
        }

        controllerView.translationControlX.listeners += fun(translationX: Float) {
            coordinateSystemView.cube.translate(Vector(translationX, 0f, 0f, 1f))
        }

        controllerView.renderGridOptionChangedListener = fun(enable: Boolean) {
            coordinateSystemView.enableGrid(enable)
        }
    }
}
