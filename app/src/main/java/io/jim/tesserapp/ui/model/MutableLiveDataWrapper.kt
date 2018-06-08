package io.jim.tesserapp.ui.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

/**
 * A wrapper for [MutableLiveData] slightly more convenient to use in Kotlin,
 * in view of Nullability and Closures.
 */
class MutableLiveDataWrapper<T> : MutableLiveData<T>() {

    /**
     * Return the current non-null value.
     *
     * The current value is always null when either explicitly set or the whole live data
     * has not been initialized yet, i.e. hasn't received a call to [setValue] yet.
     *
     * @throws RuntimeException If current value is `null`.
     */
    override fun getValue(): T {
        return super.getValue()
                ?: throw RuntimeException("Live data is null")
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
            callback(it ?: throw RuntimeException("Live data is null during observation"))
        })
    }

}
