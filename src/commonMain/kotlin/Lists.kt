package com.tomuvak.util

/**
 * Returns a list with the same size and elements as the receiver list [this], except that the element at the given
 * [index] (which has to be a valid index for the list) is the given [newValue].
 *
 * For replacing the elements at multiple indices, the other overload might be more efficient than multiple invocations
 * of this one.
 */
fun <T> List<T>.replaceAt(index: Int, newValue: T): List<T> {
    if (index !in indices) throw IndexOutOfBoundsException("Index $index out of bounds (size: $size)")
    return mapIndexed { i, oldValue -> if (i == index) newValue else oldValue }
}

/**
 * Returns a list with the same size and elements as the receiver list [this], except that for any pair of index and new
 * value in the given [indexToNewValue], the element at the index given is the new value given. Each of the indices has
 * to be a valid index for the list, and the indices must be distinct.
 *
 * For replacing the element at a single index, the other overload might be more efficient than this one.
 */
inline fun <reified T> List<T>.replaceAt(vararg indexToNewValue: Pair<Int, T>): List<T> {
    val replacements = mutableMapOf<Int, T>()
    for ((index, newValue) in indexToNewValue) {
        if (index !in indices) throw IndexOutOfBoundsException("Index $index out of bounds (size: $size)")
        if (index in replacements) throw IllegalArgumentException("Repeating index: $index")
        replacements[index] = newValue
    }
    return mapIndexed { index, oldValue -> replacements.getOr(index, oldValue) }
}
