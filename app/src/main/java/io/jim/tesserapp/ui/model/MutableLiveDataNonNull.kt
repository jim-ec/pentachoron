package io.jim.tesserapp.ui.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.jim.tesserapp.util.synchronized

/**
 * A wrapper for [MutableLiveData] whose value can never be `null`.
 *
 * Additionally, [setValue] and [getValue] are thread safe.
 *
 * @constructor Creates a live-data with an initial value.
 */
open class MutableLiveDataNonNull<T : Any>(initialValue: T) : MutableLiveData<T>() {
    
    init {
        value = initialValue
    }
    
    /**
     * Return the current *non-null* value.
     */
    override fun getValue(): T {
        return synchronized {
            super.getValue()!!
        }
    }
    
    /**
     * Set the *non-null* value to this live data.
     */
    override fun setValue(value: T) {
        synchronized {
            super.setValue(value)
        }
    }
    
    /**
     * Install an observer bound to [lifecycleOwner]'s life duration.
     *
     * @param callback Called upon value changes.
     */
    fun observeNonNull(lifecycleOwner: LifecycleOwner, callback: (T) -> Unit) {
        synchronized {
            super.observe(lifecycleOwner, Observer {
                callback(it!!)
            })
        }
    }
    
    /**
     * Install an immortal observer.
     *
     * @param callback Called upon value changes.
     */
    fun observeForeverNonNull(callback: (T) -> Unit) {
        synchronized {
            super.observeForever {
                callback(it!!)
            }
        }
    }
    
}
