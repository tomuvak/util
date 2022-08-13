package com.tomuvak.util

/**
 * Returns a list with the same size and elements as the receiver list [this], except that the element at the given
 * [index] (which has to be a valid index for the list) is the given [newValue].
 */
fun <T> List<T>.replaceAt(index: Int, newValue: T): List<T> {
    if (index !in indices) throw IndexOutOfBoundsException("Index $index out of bounds (size: $size)")
    return mapIndexed { i, oldValue -> if (i == index) newValue else oldValue }
}
