package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.axis
import io.jim.tesserapp.geometry.grid
import io.jim.tesserapp.math.common.Smoothed
import io.jim.tesserapp.math.matrix.Matrix
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
                // Transform geometry in each frame relatively,
                // by using the difference value returned from the smooth-delegates:
                this@MainViewModel.synchronized {
                    Matrix.rotation(5, 1, 2, rotationX.smoothed * Math.PI) *
                            Matrix.rotation(5, 2, 0, rotationY.smoothed * Math.PI) *
                            Matrix.rotation(5, 0, 1, rotationZ.smoothed * Math.PI) *
                            Matrix.rotation(5, 0, 3, rotationQ.smoothed * Math.PI) *
                            Matrix.translation(5, VectorN(
                                    translationX.smoothed,
                                    translationY.smoothed,
                                    translationZ.smoothed,
                                    translationQ.smoothed
                            ))
                }
                
            }
    ).apply {
        
        addQuadrilateral(
                VectorN(1.0, 1.0, 1.0, 0.0),
                VectorN(-1.0, 1.0, 1.0, 0.0),
                VectorN(-1.0, -1.0, 1.0, 0.0),
                VectorN(1.0, -1.0, 1.0, 0.0),
                color = Geometry.Color.ACCENT
        )
        
        extrude(
                direction = VectorN(0.0, 0.0, -2.0, 0.0),
                keepColors = true,
                connectorColor = Geometry.Color.ACCENT
        )
    }
    
    /**
     * The grid geometry representing a cartesian coordinate system unit grid.
     */
    val gridGeometry = Geometry(name = "Grid").apply {
        grid()
    }
    
    /**
     * Enable or disable grid rendering.
     */
    var enableGrid: Boolean
        set(value) {
            println("Toggle grid: $value")
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
    val axisGeometry = Geometry(name = "Axis").apply {
        axis()
    }
    
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
    val rotationX =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Y rotation, in units of PI.
     */
    val rotationY =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Z rotation, in units of PI.
     */
    val rotationZ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Q rotation, in units of PI.
     */
    val rotationQ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * X translation.
     */
    val translationX =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Y translation.
     */
    val translationY =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Z translation.
     */
    val translationZ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Q translation.
     */
    val translationQ = SmoothedLiveData(
            initialValue = 2.0,
            delegationMode = Smoothed.DelegationMode.ABSOLUTE)
    
    /**
     * Camera distance.
     */
    val cameraDistance =
            SmoothedLiveData(initialValue = 8.0)
    
    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    val horizontalCameraRotation =
            SmoothedLiveData(initialValue = Math.PI / 3.0, transitionInterval = 80.0)
    
    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    val verticalCameraRotation =
            SmoothedLiveData(initialValue = -Math.PI / 8.0, transitionInterval = 80.0)
    
    var colorResolver: (color: Geometry.Color) -> Int = { Color.BLACK }
    
    enum class RenderMode {
        WIREFRAME_PROJECTION,
        COLLAPSE_Z
    }
    
    var renderMode = RenderMode.WIREFRAME_PROJECTION
    
}
