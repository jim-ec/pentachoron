package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import io.jim.tesserapp.util.synchronized

/**
 * A view model with synchronizing helper utilities.
 *
 * To access live data in a thread-safe manner, you have to use monitors.
 *
 * You can even pass a monitor object to other classes, so they can access the view model
 * instance *only* through that synchronizing monitor, instead of passing direct references
 * to the view model.
 */
open class SynchronizedViewModel : ViewModel() {

    /**
     * Serves as a thread-safe interface to the [SynchronizedViewModel].
     * The actual reference to the view model is not directly accessible.
     *
     * Instead, in order to access the view model, you have to use the monitor's invoke function.
     */
    inner class Monitor {

        /**
         * Calls [f] with the associated view model reference passed as the receiver to it.
         *
         * During the execution of [f], the passed view model can safely be referenced.
         * Storing that reference is prohibited, as it diminishes the safety provided
         * by this monitor.
         *
         * @param f
         * Function to be executed.
         * The view model reference is passed to it as the receiver.
         * The expected type of the view model class to be passed should be explicitly expressed,
         * as this enables automatic down-casting.
         */
        inline operator fun <reified T : SynchronizedViewModel> invoke(
                crossinline f: T.() -> Unit
        ) {
            this@SynchronizedViewModel.synchronized {
                (this@SynchronizedViewModel as? T ?: throw RuntimeException()).f()
            }
        }

    }

    /**
     * Calls [f] while synchronizing the view model.
     *
     * During the execution of [f], the passed view model can safely be referenced.
     *
     * @param f
     * Function to be executed.
     * The view model reference is passed it as the parameter.
     * The expected type of the view model class to be passed should be explicitly expressed,
     * as this enables automatic down-casting.
     *
     * @return
     * The return value of [f].
     */
    inline operator fun <R> invoke(crossinline f: () -> R) =
            synchronized {
                f()
            }

}
