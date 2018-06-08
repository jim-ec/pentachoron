package io.jim.tesserapp.ui.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

/**
 * A wrapper for [MutableLiveData] whose value can never be `null`.
 *
 * @constructor Creates a live-data with an initial value.
 */
class MutableLiveDataNonNull<T>(initialValue: T) : MutableLiveData<T>() {

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
     * Whenever the internal value changes, [callback] is called with the new value.
     *
     * Value changes to `null` are considered as an error using this specific function,
     * therefore the value received by [callback] is always **non-null**.
     *
     * @throws RuntimeException If value changes to `null`.
     */
    fun observe(lifecycleOwner: LifecycleOwner, callback: (T) -> Unit) {
        super.observe(lifecycleOwner, Observer {
            callback(it!!)
        })
    }

}
