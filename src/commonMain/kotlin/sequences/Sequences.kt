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

/**
 * Returns the single element in the receiver sequence [this], or throws if the sequence is empty. Not supposed to be
 * called on a sequence with more than one element – but this function makes no effort to verify that that's not the
 * case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [first], but signals to the reader of calling code that the point is not to take the _first_
 * element (of potentially multiple elements), but rather the _only_ element (where the fact that there can't be
 * multiple elements has somehow been established prior to the call).
 */
fun <T> Sequence<T>.singleNoVerify(): T = first()

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or throws if there
 * is no element in the sequence which satisfies the predicate. Not supposed to be called on a sequence with more than
 * one element satisfying the predicate – but this function makes no effort to verify that that's not the case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [first], but signals to the reader of calling code that the point is not to take the _first_
 * element (of potentially multiple elements) which satisfies the predicate, but rather the _only_ element which does
 * (where the fact that there can't be multiple elements satisfying the predicate has somehow been established prior to
 * the call).
 */
fun <T> Sequence<T>.singleNoVerify(predicate: (T) -> Boolean): T = first(predicate)

/**
 * Returns the single element in the receiver sequence [this], or `null` if the sequence is empty. Not supposed to be
 * called on a sequence with more than one element – but this function makes no effort to verify that that's not the
 * case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [firstOrNull], but signals to the reader of calling code that the point is not to take the
 * _first_ element (of potentially multiple elements), but rather the _only_ element (where the fact that there can't be
 * multiple elements has somehow been established prior to the call).
 */
fun <T> Sequence<T>.singleNoVerifyOrNull(): T? = firstOrNull()

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or `null` if there
 * is no element in the sequence which satisfies the predicate. Not supposed to be called on a sequence with more than
 * one element satisfying the predicate – but this function makes no effort to verify that that's not the case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [firstOrNull], but signals to the reader of calling code that the point is not to take the
 * _first_ element (of potentially multiple elements) which satisfies the predicate, but rather the _only_ element which
 * does (where the fact that there can't be multiple elements satisfying the predicate has somehow been established
 * prior to the call).
 */
fun <T> Sequence<T>.singleNoVerifyOrNull(predicate: (T) -> Boolean): T? = firstOrNull(predicate)

/**
 * Returns (a sequence equivalent to) the receiver sequence [this] if it is not empty, or `null` if it is. Other than
 * the initial check for emptiness, this operation is _intermediate_ and _stateless_.
 */
fun <T> Sequence<T>.ifNotEmpty(): Sequence<T>? {
    val iterator = iterator()
    return if (iterator.hasNext()) object : Sequence<T> {
        var first = true
        override fun iterator(): Iterator<T> {
            if (first) {
                first = false
                return (sequenceOf(iterator.next()) + iterator.asSequence()).iterator()
            }
            return this@ifNotEmpty.iterator()
        }
    } else null
}

/**
 * Returns a sequence which yields the exact same elements as the receiver sequence [this] and is iterable multiple
 * times – even if the original sequence is not. The original sequence is guaranteed not to be iterated over more than
 * once, and, during the first (and only) iteration, not to be iterated over further than is necessary (that is this
 * operation, while not _stateless_, is _intermediate_).
 */
fun <T> Sequence<T>.cached(): Sequence<T> = if (this is CachingSequence<T>) this else CachingSequence(this)

/**
 * Yields all elements of the receiver sequence [this] before the first one which is not of type [T] (if any; yields all
 * elements if they're all of type T). This operation is _intermediate_ and _stateless_.
 */
@Suppress("UNCHECKED_CAST") inline fun <reified T> Sequence<*>.takeWhileIsInstance(): Sequence<T> =
    takeWhile { it is T } as Sequence<T>
