package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.mapDifference

class MainViewModel : ViewModel() {

    val featuredGeometry = Geometry()

    val rotationX = MutableLiveDataNonNull(0.0).apply {
        observeForeverNonNull(mapDifference(value) { difference ->
            featuredGeometry.transform.rotateX(difference)
        })
    }

    /*
    val rotationY = MutableLiveDataNonNull(0.0)
    val rotationZ = MutableLiveDataNonNull(0.0)
    val rotationQ = MutableLiveDataNonNull(0.0)

    val translationX = MutableLiveDataNonNull(0.0)
    val translationY = MutableLiveDataNonNull(0.0)
    val translationZ = MutableLiveDataNonNull(0.0)
    val translationQ = MutableLiveDataNonNull(0.0)
    */

    val cameraDistance = MutableLiveDataNonNull(8.0)

}
