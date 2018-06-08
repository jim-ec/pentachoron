package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.mapDifference

class MainViewModel : ViewModel() {

    val featuredGeometry = Geometry()

    /**
     * X rotation.
     */
    val rotationX = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.rotateX(difference * Math.PI)
        })
    }

    /**
     * Y rotation.
     */
    val rotationY = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.rotateY(difference * Math.PI)
        })
    }

    /**
     * Z rotation.
     */
    val rotationZ = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.rotateZ(difference * Math.PI)
        })
    }

    /**
     * Q rotation.
     */
    val rotationQ = MutableLiveDataNonNull(0.0)

    /**
     * X translation.
     */
    val translationX = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.translateX(difference)
        })
    }

    /**
     * Y translation.
     */
    val translationY = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.translateY(difference)
        })
    }

    /**
     * Z translation.
     */
    val translationZ = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.translateZ(difference)
        })
    }

    /**
     * Q translation.
     */
    val translationQ = MutableLiveDataNonNull(0.0)

    /**
     * Camera distance.
     */
    val cameraDistance = MutableLiveDataNonNull(8.0)

}
