package io.jim.tesserapp.ui.model

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.common.Smoothed

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : SynchronizedViewModel() {

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
