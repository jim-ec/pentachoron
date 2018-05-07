package io.jim.tesserapp.geometry

import io.jim.tesserapp.util.Flag

class ModelIndex {

    private val valid = Flag(false)
    private var index = -1

    fun set(modelIndex: Int) {
        if (valid.isSet())
            throw RuntimeException("Cannot set model index, is already set, need to be unset")
        valid.set()
        index = modelIndex
    }

    fun get() =
            if (valid.isUnset()) throw RuntimeException("Cannot get model index, not set yet")
            else index

    fun unset() {
        if (valid.isUnset()) throw RuntimeException("Cannot unset model index, is already unset")
        valid.unset()
    }

}
