package io.jim.tesserapp.math

import kotlin.reflect.KProperty

interface Indexable<T, V> {

    operator fun get(index: Int): V

    operator fun set(index: Int, value: V)

}


/**
 * Enables property aliasing to components with a specific [index],
 * so 'x' aliases to the first component and so on.
 */
class IndexAlias<T : Indexable<T, V>, V>(private val index: Int) {

    operator fun getValue(thisRef: T?, property: KProperty<*>): V {
        return thisRef?.get(index)!!
    }

    operator fun setValue(thisRef: T?, property: KProperty<*>, value: V) {
        thisRef?.set(index, value)
    }

}
