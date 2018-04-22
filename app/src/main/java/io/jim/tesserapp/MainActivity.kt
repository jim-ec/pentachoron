package io.jim.tesserapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

        controllerView += cubeView
    }
}
