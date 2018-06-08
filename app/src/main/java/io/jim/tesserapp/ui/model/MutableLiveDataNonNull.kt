package io.jim.tesserapp.ui.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

/**
 * A wrapper for [MutableLiveData] whose value can never be `null`.
 *
 * @constructor Creates a live-data with an initial value.
 */
open class MutableLiveDataNonNull<T>(initialValue: T) : MutableLiveData<T>() {

    init {
        value = initialValue
    }

    /**
     * Return the current `non-null` value.
     */
    override fun getValue(): T {
        return super.getValue()!!
    }

    /**
     * Install an observer bound to [lifecycleOwner]'s life duration.
     *
     * @param callback Called upon value changes.
     */
    fun observeNonNull(lifecycleOwner: LifecycleOwner, callback: (T) -> Unit) {
        super.observe(lifecycleOwner, Observer {
            callback(it!!)
        })
    }

    /**
     * Install an immortal observer.
     *
     * @param callback Called upon value changes.
     */
    fun observeForeverNonNull(callback: (T) -> Unit) {
        super.observeForever {
            callback(it!!)
        }
    }

}
