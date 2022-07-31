package com.tomuvak.util

/**
 * Returns a list of all elements of the receiver collection [this] except for the last [n] (which is not allowed to be
 * negative) ones (resulting in an empty list in case there are only [n] elements or less to begin with).
 *
 * For any given [n], a collection is supposedly the concatenation of its [dropLast]`(n)` and its [takeLast]`(n)`.
 */
fun <T> Collection<T>.dropLast(n: Int): List<T> {
    require(n >= 0) { "Can't drop a negative number of elements ($n)"}
    return take((size - n).coerceAtLeast(0))
}

/**
 * Returns a list of the last [n] (which is not allowed to be negative) elements of the receiver collection [this]
 * (resulting in a list of all elements in case there are only [n] elements or less to begin with).
 *
 * For any given [n], a collection is supposedly the concatenation of its [dropLast]`(n)` and its [takeLast]`(n)`.
 */
fun <T> Collection<T>.takeLast(n: Int): List<T> {
    require(n >= 0) { "Can't take a negative number of elements ($n)"}
    return drop((size - n).coerceAtLeast(0))
}
