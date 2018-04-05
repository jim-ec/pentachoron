package io.jim.tesserapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.ui.ControllerView
import io.jim.tesserapp.ui.CubeView

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var cubeView: CubeView
    private lateinit var controllerView: ControllerView

    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cubeView = findViewById(R.id.cubeView)
        controllerView = findViewById(R.id.controllerView)

        controllerView.cameraControlDistance.listeners += fun(distance: Float) {
            synchronized(cubeView.coordinateSystemView.sharedRenderData) {
                cubeView.coordinateSystemView.sharedRenderData.cameraDistance = distance
            }
        }

        controllerView.rotationControlXZ.listeners += fun(rotation: Float) {
            synchronized(cubeView.coordinateSystemView.sharedRenderData) {
                cubeView.cube.rotationZX(rotation)
            }
        }

        controllerView.rotationControlXY.listeners += fun(rotation: Float) {
            synchronized(cubeView.coordinateSystemView.sharedRenderData) {
                cubeView.cube.rotationYX(rotation)
            }
        }

        controllerView.translationControlX.listeners += fun(translationX: Float) {
            synchronized(cubeView.coordinateSystemView.sharedRenderData) {
                cubeView.cube.translate(Vector(translationX, 0f, 0f, 1f))
            }
        }

        controllerView.renderGridOptionChangedListener = fun(enable: Boolean) {
            synchronized(cubeView.coordinateSystemView.sharedRenderData) {
                cubeView.coordinateSystemView.enableGrid(enable)
            }
        }
    }
}
