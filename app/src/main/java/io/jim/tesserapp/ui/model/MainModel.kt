package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel

class MainModel : ViewModel() {

    val cameraDistance = MutableLiveDataNonNull(8.0)

}
