package io.jim.tesserapp.ui.model

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.common.Smoothed

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : SynchronizedViewModel() {

    /**
     * The featured geometry.
     */
    val featuredGeometry = Geometry(
            onTransformUpdate = {
                // Transform geometry in each frame relatively,
                // by using the difference value returned from the smooth-delegates:

                this@MainViewModel {
                    rotateX(rotationX.smoothed * java.lang.Math.PI)
                    rotateY(rotationY.smoothed * java.lang.Math.PI)
                    rotateZ(rotationZ.smoothed * java.lang.Math.PI)

                    translateX(translationX.smoothed)
                    translateY(translationY.smoothed)
                    translateZ(translationZ.smoothed)
                }

            }
    )

    /**
     * The grid geometry representing a cartesian coordinate system unit grid.
     */
    val gridGeometry = Geometry().apply {
        name = "Grid"
    }

    /**
     * Enable or disable grid rendering.
     */
    var enableGrid: Boolean
        set(value) {
            println("Toggle grid: $value")
            if (value) {
                geometries += gridGeometry
            }
            else {
                geometries -= gridGeometry
            }
        }
        get() = geometries.contains(gridGeometry)

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
    val geometries = HashSet<Geometry>().apply {
        add(featuredGeometry)
    }

    /**
     * X rotation, in units of PI.
     */
    val rotationX =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * Y rotation, in units of PI.
     */
    val rotationY =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * Z rotation, in units of PI.
     */
    val rotationZ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * Q rotation, in units of PI.
     */
    val rotationQ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * X translation.
     */
    val translationX =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * Y translation.
     */
    val translationY =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * Z translation.
     */
    val translationZ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

    /**
     * Q translation.
     */
    val translationQ =
            SmoothedLiveData(delegationMode = Smoothed.DelegationMode.RELATIVE_TO_LAST_READ)

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

}
