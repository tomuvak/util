package com.tomuvak.util.sequences

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
fun <T> Sequence<T>.cached(): Sequence<T> = CachingSequence(this)

/**
 * Yields all elements of the receiver sequence [this] before the first one which is not of type [T] (if any; yields all
 * elements if they're all of type T). This operation is _intermediate_ and _stateless_.
 */
@Suppress("UNCHECKED_CAST") inline fun <reified T> Sequence<*>.takeWhileIsInstance(): Sequence<T> =
    takeWhile { it is T } as Sequence<T>
