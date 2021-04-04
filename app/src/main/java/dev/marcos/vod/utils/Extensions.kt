package dev.marcos.vod.utils

object Extensions {

    fun <T> Collection<T>.addOnTop(element: T): List<T> = ArrayList<T>(size + 1)
        .apply { add(element) }
        .also { it.addAll(this) }

}