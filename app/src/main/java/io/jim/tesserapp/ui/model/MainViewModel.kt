package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.common.Smoothed

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : ViewModel() {

    val featuredGeometry = Geometry(
            onTransformUpdate = {
                // Transform geometry in each frame relatively,
                // by using the difference value returned from the smooth-delegates:

                rotateX(rotationX.smoothed * Math.PI)
                rotateY(rotationY.smoothed * Math.PI)
                rotateZ(rotationZ.smoothed * Math.PI)

                translateX(translationX.smoothed)
                translateY(translationY.smoothed)
                translateZ(translationZ.smoothed)
            }
    )

    /**
     * X rotation, in units of PI.
     */
    val rotationX = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Y rotation, in units of PI.
     */
    val rotationY = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Z rotation, in units of PI.
     */
    val rotationZ = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Q rotation, in units of PI.
     */
    val rotationQ = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * X translation.
     */
    val translationX = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Y translation.
     */
    val translationY = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Z translation.
     */
    val translationZ = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Q translation.
     */
    val translationQ = SmoothedLiveData(0.0, Smoothed.DelegationMode.RELATIVE)

    /**
     * Camera distance.
     */
    val cameraDistance = SmoothedLiveData(8.0, Smoothed.DelegationMode.ABSOLUTE)

}
