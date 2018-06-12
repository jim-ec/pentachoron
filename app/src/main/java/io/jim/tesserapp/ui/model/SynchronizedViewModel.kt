package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.util.synchronized

typealias SynchronizedViewModelMonitor<T> = ((T) -> Unit) -> Unit

open class SynchronizedViewModel : ViewModel() {

    inline fun <reified T> monitor() =
            { f: (viewModel: T) -> Unit ->
                this@SynchronizedViewModel.synchronized {
                    f(this@SynchronizedViewModel as? T ?: throw RuntimeException())
                }
            }

    inline operator fun invoke(crossinline f: () -> Unit) {
        synchronized {
            f()
        }
    }

}
