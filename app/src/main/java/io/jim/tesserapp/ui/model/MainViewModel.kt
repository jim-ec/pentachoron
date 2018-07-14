package io.jim.tesserapp.ui.model

import androidx.lifecycle.ViewModel
import io.jim.tesserapp.geometry.*
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.util.synchronized

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : ViewModel() {
    
    /**
     * List containing all geometries.
     *
     * The geometries are kept inside a hash-set instead of in a simply list because
     * geometries added multiple times should not result in the same geometry stored
     * more once in the set.
     *
     * This happens because this set outlives the UI, and the UI may initially add
     * geometry to the set each time the UI is set up.
     */
    val geometries = HashSet<Geometry>()
    
    /**
     * Create geometry, replacing any previously existing one.
     */
    fun createGeometries(
            featuredGeometryName: String,
            enableGrid: Boolean
    ) {
        // Reset any previous state:
        geometries.clear()
        
        geometries += Geometry(
                name = featuredGeometryName,
                isFourDimensional = true,
                onTransformUpdate = {
                    synchronized {
                        Transform(
                                rotationX = rotationX.smoothed * Math.PI,
                                rotationY = rotationY.smoothed * Math.PI,
                                rotationZ = rotationZ.smoothed * Math.PI,
                                rotationQ = rotationQ.smoothed * Math.PI,
                                translationX = translationX.smoothed,
                                translationY = translationY.smoothed,
                                translationZ = translationZ.smoothed,
                                translationQ = translationQ.smoothed
                        )
                    }
                },
                lines = extruded(
                        positions = extruded(
                                quadrilateral(
                                        Position(2.0, 2.0, 2.0, 0.0),
                                        Position(-2.0, 2.0, 2.0, 0.0),
                                        Position(-2.0, -2.0, 2.0, 0.0),
                                        Position(2.0, -2.0, 2.0, 0.0)),
                                direction = Position(0.0, 0.0, -4.0, 0.0)
                        ),
                        direction = Position(0.0, 0.0, 0.0, 2.0)
                ),
                color = SymbolicColor.ACCENT
        )
        
        geometries += Geometry("X-Axis",
                lines = listOf(Line(Position(0.0, 0.0, 0.0, 0.0), Position(1.0, 0.0, 0.0, 0.0))),
                color = SymbolicColor.X)
        
        geometries += Geometry("Y-Axis",
                lines = listOf(Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 1.0, 0.0, 0.0))),
                color = SymbolicColor.Y)
        
        geometries += Geometry("Z-Axis",
                lines = listOf(Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 0.0, 1.0, 0.0))),
                color = SymbolicColor.Z)
        
        if (enableGrid) {
            geometries += Geometry(
                    name = "Grid",
                    lines = gridOmitAxisIndicator(),
                    onTransformUpdate = { Transform() },
                    color = SymbolicColor.PRIMARY
            )
        }
    }
    
    /**
     * X rotation, in units of PI.
     */
    val rotationX = SmoothedLiveData()
    
    /**
     * Y rotation, in units of PI.
     */
    val rotationY = SmoothedLiveData()
    
    /**
     * Z rotation, in units of PI.
     */
    val rotationZ = SmoothedLiveData()
    
    /**
     * Q rotation, in units of PI.
     */
    val rotationQ = SmoothedLiveData()
    
    /**
     * X translation.
     */
    val translationX = SmoothedLiveData()
    
    /**
     * Y translation.
     */
    val translationY = SmoothedLiveData()
    
    /**
     * Z translation.
     */
    val translationZ = SmoothedLiveData()
    
    /**
     * Q translation.
     */
    val translationQ = SmoothedLiveData(initialValue = 3.7)
    
    /**
     * Camera distance.
     */
    val cameraDistance = SmoothedLiveData(initialValue = 5.0)
    
    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    val horizontalCameraRotation = SmoothedLiveData(
            initialValue = Math.PI / 3.0,
            transitionInterval = 80.0
    )
    
    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    val verticalCameraRotation = SmoothedLiveData(
            initialValue = -Math.PI / 8.0,
            transitionInterval = 80.0
    )
    
    val cameraFovX = SmoothedLiveData(initialValue = 60.0, transitionInterval = 80.0)
    
    var symbolicColorMapping = SymbolicColorMapping(
            primary = Color.BLACK,
            accent = Color.BLACK,
            x = Color.BLACK,
            y = Color.BLACK,
            z = Color.BLACK,
            q = Color.BLACK
    )
    
}
