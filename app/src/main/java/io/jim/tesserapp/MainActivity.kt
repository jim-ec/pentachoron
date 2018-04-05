package io.jim.tesserapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.gui.ControllerView
import io.jim.tesserapp.gui.CoordinateSystemView
import kotlin.math.PI

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

        controllerView.rotationXZListener = fun(rotation: Float) {
            coordinateSystemView.cube.rotationZX(rotation * PI.toFloat())
        }

        controllerView.renderGridOptionChangedListener = fun(enable: Boolean) {
            coordinateSystemView.enableGrid(enable)
        }
    }
}
