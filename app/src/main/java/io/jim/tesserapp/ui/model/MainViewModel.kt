package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.math.common.Smoothed

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : ViewModel() {

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
    var horizontalCameraRotation =
            SmoothedLiveData(initialValue = Math.PI / 3.0, transitionInterval = 80.0)

    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    var verticalCameraRotation =
            SmoothedLiveData(initialValue = -Math.PI / 8.0, transitionInterval = 80.0)

}
