package io.jim.tesserapp.math

import kotlin.reflect.KProperty


/**
 * Enables property aliasing to components with a specific [index],
 * so 'x' aliases to the first component and so on.
 */
class IndexAlias<in T : Indexable<V>, V>(private val index: Int) {

    /**
     * Get the property at this index.
     */
    operator fun getValue(thisRef: T?, property: KProperty<*>): V {
        return thisRef?.get(index)!!
    }

    /**
     * Set this property at this index to [value].
     */
    operator fun setValue(thisRef: T?, property: KProperty<*>, value: V) {
        thisRef?.set(index, value)
    }

}

/**
 * For a class to be able to have index aliases, it must implement this interface.
 */
interface Indexable<V> {

    /**
     * Return a property at a given [index].
     */
    operator fun get(index: Int): V

    /**
     * Set a property at [index] to [value].
     */
    operator fun set(index: Int, value: V)

}
