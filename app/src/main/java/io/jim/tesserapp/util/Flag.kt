package io.jim.tesserapp.util

data class Flag(

        private var flag: Boolean

) {

    operator fun not() = !flag

    fun isSet() = flag

    fun isUnset() = !flag

    fun set() {
        setTo(true)
    }

    fun unset() {
        setTo(false)
    }

    private fun setTo(newFlag: Boolean) {
        if (flag == newFlag) {
            return
        }
        flag = newFlag
    }

}
