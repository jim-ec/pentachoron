package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.geometry.Geometry

class MainViewModel : ViewModel() {

    val featuredGeometry = Geometry(
            onTransformUpdate = {
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
    val rotationX = SmoothedLiveData(0.0)

    /**
     * Y rotation, in units of PI.
     */
    val rotationY = SmoothedLiveData(0.0)

    /**
     * Z rotation, in units of PI.
     */
    val rotationZ = SmoothedLiveData(0.0)

    /**
     * Q rotation, in units of PI.
     */
    val rotationQ = SmoothedLiveData(0.0)

    /**
     * X translation.
     */
    val translationX = SmoothedLiveData(0.0)

    /**
     * Y translation.
     */
    val translationY = SmoothedLiveData(0.0)

    /**
     * Z translation.
     */
    val translationZ = SmoothedLiveData(0.0)

    /**
     * Q translation.
     */
    val translationQ = SmoothedLiveData(0.0)

    /**
     * Camera distance.
     */
    val cameraDistance = MutableLiveDataNonNull(8.0)

}
