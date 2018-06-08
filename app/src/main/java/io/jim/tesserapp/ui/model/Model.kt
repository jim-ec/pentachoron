package io.jim.tesserapp.ui.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class Model : ViewModel() {

    val cameraDistanceData = MutableLiveData<Double>().apply { value = 8.0 }

    var cameraDistance: Double
        get() {
            return cameraDistanceData.value!!
        }
        set(value) {
            cameraDistanceData.value = value
        }

}
