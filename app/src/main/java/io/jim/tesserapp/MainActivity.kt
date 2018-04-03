package io.jim.tesserapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.gui.ControllerView
import io.jim.tesserapp.gui.CoordinateSystemView
import kotlin.math.PI

class MainActivity : AppCompatActivity() {

    lateinit var coordinateSystemView: CoordinateSystemView
    lateinit var controllerView: ControllerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coordinateSystemView = findViewById(R.id.coordinateSystemView)
        controllerView = findViewById(R.id.controllerView)

        controllerView.rotationXZListener = fun(rotation: Double) {
            coordinateSystemView.cube.rotationZX(rotation * PI)
            println("Rotate cube in XZ-Plane: ${rotation * PI}")
        }
    }
}
