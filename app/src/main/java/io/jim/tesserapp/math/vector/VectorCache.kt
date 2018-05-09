package io.jim.tesserapp.math.vector

class VectorCache<T : VectorN>(
        private val generator: () -> T
) {

    private val vectors = HashMap<Int, T>()

    operator fun get(index: Int) =
            vectors[index] ?: generator().also {
                vectors[index] = it
            }

}
