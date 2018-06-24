package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import io.jim.tesserapp.geometry.*
import io.jim.tesserapp.math.matrix.RotationPlane
import io.jim.tesserapp.math.matrix.rotation
import io.jim.tesserapp.math.matrix.transformChain
import io.jim.tesserapp.math.matrix.translation
import io.jim.tesserapp.math.vector.VectorN
import io.jim.tesserapp.util.synchronized

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : ViewModel() {
    
    /**
     * The featured geometry.
     */
    val featuredGeometry = Geometry(
            name = "Featured Geometry",
            isFourDimensional = true,
            onTransformUpdate = {
                synchronized {
                    transformChain(listOf(
                            rotation(5, RotationPlane.AROUND_X, rotationX.smoothed * Math.PI),
                            rotation(5, RotationPlane.AROUND_Y, rotationY.smoothed * Math.PI),
                            rotation(5, RotationPlane.AROUND_Z, rotationZ.smoothed * Math.PI),
                            rotation(5, RotationPlane.XQ, rotationQ.smoothed * Math.PI),
                            translation(5, VectorN(
                                    translationX.smoothed,
                                    translationY.smoothed,
                                    translationZ.smoothed,
                                    translationQ.smoothed
                            ))
                    ))
                }
            },
            points = extruded(
                    quadrilateral(
                            VectorN(2.0, 2.0, 2.0, 0.0),
                            VectorN(-2.0, 2.0, 2.0, 0.0),
                            VectorN(-2.0, -2.0, 2.0, 0.0),
                            VectorN(2.0, -2.0, 2.0, 0.0),
                            color = SymbolicColor.ACCENT),
                    direction = VectorN(0.0, 0.0, -4.0, 0.0),
                    keepColors = true,
                    connectorColor = SymbolicColor.ACCENT
            )
    )
    
    /**
     * The grid geometry representing a cartesian coordinate system unit grid.
     */
    val gridGeometry = Geometry(name = "Grid", points = gridOmitAxisIndicator())
    
    /**
     * Enable or disable grid rendering.
     */
    var enableGrid: Boolean
        set(value) {
            if (value) {
                geometries += gridGeometry
            } else {
                geometries -= gridGeometry
            }
        }
        get() = geometries.contains(gridGeometry)
    
    /**
     * The axis geometry representing the origin of the cartesian coordinate system.
     */
    val axisGeometry = Geometry(name = "Axis", points = axis())
    
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
    val geometries = HashSet<Geometry>().also {
        it += featuredGeometry
        it += axisGeometry
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
    val cameraDistance = SmoothedLiveData(initialValue = 3.0)
    
    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    val horizontalCameraRotation = SmoothedLiveData(initialValue = Math.PI / 3.0, transitionInterval = 80.0)
    
    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    val verticalCameraRotation = SmoothedLiveData(initialValue = -Math.PI / 8.0, transitionInterval = 80.0)
    
    var colorResolver: (color: SymbolicColor) -> Int = { Color.BLACK }
    
    var visualizationMode = VisualizationMode.WIREFRAME_PROJECTION
    
}
