package com.tomuvak.util.sequences

/**
 * Returns the single element in the receiver sequence [this], or `null` if the sequence is empty.
 *
 * Throws if the sequence contains more than one element.
 *
 * This operation is _terminal_.
 *
 * This is different from the standard library's [singleOrNull] in that this function throws (rather than return `null`)
 * in case the sequence contains more than one element.
 */
fun <T> Sequence<T>.singleOrNullIfEmpty(): T? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    val ret = iterator.next()
    return if (iterator.hasNext()) throw IllegalArgumentException("Sequence has more than one element.") else ret
}

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or `null` if the
 * sequence contains no element satisfying the predicate.
 *
 * Throws if the sequence contains more than one element satisfying the predicate.
 *
 * This operation is _terminal_.
 *
 * This is different from the standard library's [singleOrNull] in that this function throws (rather than return `null`)
 * in case the sequence contains more than one element satisfying the predicate.
 */
fun <T> Sequence<T>.singleOrNullIfNone(predicate: (T) -> Boolean): T? {
    var ret: T? = null
    var found = false
    for (element in this)
        if (predicate(element)) {
            if (found) throw IllegalArgumentException("Sequence contains more than one matching element.")
            found = true
            ret = element
        }
    return ret
}

/**
 * Returns the single element in the receiver sequence [this], or `null` if the sequence contains multiple elements.
 *
 * Throws if the sequence is empty.
 *
 * This operation is _terminal_.
 *
 * This is different from the standard library's [singleOrNull] in that this function throws (rather than return `null`)
 * in case the sequence is empty.
 */
fun <T> Sequence<T>.singleOrNullIfMultiple(): T? {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException("Sequence is empty.")
    val ret = iterator.next()
    return if (iterator.hasNext()) null else ret
}

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or `null` if the
 * sequence contains multiple elements satisfying the predicate.
 *
 * Throws if the sequence contains no element satisfying the predicate.
 *
 * This operation is _terminal_.
 *
 * This is different from the standard library's [singleOrNull] in that this function throws (rather than return `null`)
 * in case the sequence contains no element satisfying the predicate.
 */
fun <T> Sequence<T>.singleOrNullIfMultiple(predicate: (T) -> Boolean): T? {
    var ret: T? = null
    var found = false
    for (element in this)
        if (predicate(element)) {
            if (found) return null
            found = true
            ret = element
        }
    return if (found) ret else throw NoSuchElementException("Sequence contains no element matching the predicate.")
}

/**
 * Returns the single element in the receiver sequence [this], or `null` if the sequence is empty or contains multiple
 * elements.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to the standard library's [singleOrNull], but has a more explicit name that makes the semantics
 * clearer.
 */
fun <T> Sequence<T>.singleOrNullIfEmptyOrMultiple(): T? = singleOrNull()

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or `null` if the
 * sequence contains either no element satisfying the predicate or multiple such elements.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to the standard library's [singleOrNull], but has a more explicit name that makes the semantics
 * clearer.
 */
fun <T> Sequence<T>.singleOrNullIfNoneOrMultiple(predicate: (T) -> Boolean): T? = singleOrNull(predicate)
