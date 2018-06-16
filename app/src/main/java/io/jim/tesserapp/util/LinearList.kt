package io.jim.tesserapp.util

import java.util.*

/**
 * Modified version of an [ArrayList].
 */
class LinearList<T> : ArrayList<T>() {
    
    /**
     * Calls [f] for each list entry.
     *
     * @throws ConcurrentModificationException
     * When modifying the list while iterating over it.
     */
    inline fun indexedForEach(f: (T) -> Unit) {
        val expectedModCount = accessModCount
        for (i in 0 until size) {
            f(this[i])
            if (accessModCount != expectedModCount)
                throw ConcurrentModificationException()
        }
    }
    
    @PublishedApi
    internal var accessModCount: Int
        get() = modCount
        set(value) {
            modCount = value
        }
    
}
