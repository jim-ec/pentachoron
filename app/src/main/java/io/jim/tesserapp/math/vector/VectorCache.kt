package io.jim.tesserapp.math.vector

/**
 * Stores vector allocations of type [T], so that computations can use this cache for
 * intermediate results instead of allocating new vectors all the time.
 *
 * Vectors are allocated when the corresponding index is accessed the very first time using [get],
 * further accesses to the same index use the same underlying vector object.
 *
 * @param generator A creator function responsible for creating new vector instances.
 */
class VectorCache<T : VectorN>(
        private val generator: () -> T
) {

    /**
     * Maps indices to allocations.
     */
    private val vectors = HashMap<Int, T>()

    /**
     * Indicates whether in acquire mode, allowing acquiring cached allocations.
     */
    private var inAcquireMode = false

    /**
     * The index of the next allocation returned by [acquire], reset every times the cache enters
     * acquire mode.
     */
    private var acquireIndex = 0

    /**
     * Start allowing acquiring cached instances.
     * @throws RuntimeException If cache is already in acquire mode.
     */
    fun startAcquiring() {
        if (inAcquireMode)
            throw RuntimeException("Already in acquire mode, did you forget to call endAcquiring()?")

        inAcquireMode = true
        acquireIndex = 0
    }

    /**
     * Ends acquire mode.
     * @throws RuntimeException If cache has not entered acquire mode yet.
     */
    fun endAcquiring() {
        if (!inAcquireMode)
            throw RuntimeException("Cannot end acquire mode, did you forget to call startAcquiring()?")

        inAcquireMode = false
    }

    /**
     * Returns a allocation. Between the calls [startAcquiring] and [endAcquiring], every instance
     * returned by [acquire] resulted of a unique call to [generator].
     *
     * @throws RuntimeException If cache has not entered acquire mode.
     */
    fun acquire() =
            if (!inAcquireMode)
                throw RuntimeException("Not in acquire mode, did you forget to call startAcquiring()?")
            else
                this[acquireIndex++]

    /**
     * Either returns the allocation mapped by [index], or allocates a new vector.
     */
    private operator fun get(index: Int) =
            vectors[index] ?: generator().also {
                vectors[index] = it
            }

}
