package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel

class Model : ViewModel() {

    val cameraDistance = MutableLiveDataWrapper<Double>().apply { value = 8.0 }

}
