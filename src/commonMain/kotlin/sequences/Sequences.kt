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

/**
 * Returns a sequence which yields the exact same elements as the receiver sequence [this], in the same order, but
 * without repetitions – that is elements which have already appeared before are filtered out, and also stopping as soon
 * as [maxConsecutiveAttempts] (which has to be positive) consecutive elements have been filtered out this way, if ever.
 *
 * This is similar to the standard library's `distinct` extension function, but with the added functionality of the
 * returned sequence stopping when [maxConsecutiveAttempts] consecutive elements are repetitions of past elements. One
 * case where this is useful is when the receiver sequence [this] is potentially infinite but containing only a finite
 * number of _distinct_ elements: in that case the standard library's `.distinct()` will return a sequence which enters
 * an infinite loop (after retrieving all unique values, trying to retrieve the next value will not fail – it will
 * simply never return), whereas the sequence returned by this function will stop (note that this does **not** mean that
 * the returned sequence can never be infinite: it will be if the original sequence is itself infinite and _keeps
 * yielding **distinct** values_). Of course, this feature also means that it's possible for the returned sequence
 * _not_ to contain all distinct values contained in the original: any new value which follows more than
 * [maxConsecutiveAttempts] consecutive old values will be missed.
 *
 * This operation is _intermediate_ but _stateful_.
 */
fun <T> Sequence<T>.distinct(maxConsecutiveAttempts: Int): Sequence<T> {
    require(maxConsecutiveAttempts > 0) { "maxConsecutiveAttempts must be positive (was $maxConsecutiveAttempts)" }
    return DistinctSequence(this, maxConsecutiveAttempts)
}

private class DistinctSequence<T>(
    private val source: Sequence<T>,
    private val maxConsecutiveAttempts: Int
): Sequence<T> { override fun iterator(): Iterator<T> = DistinctIterator(source.iterator(), maxConsecutiveAttempts) }

private class DistinctIterator<T>(
    private val source: Iterator<T>,
    private val maxConsecutiveAttempts: Int
): AbstractIterator<T>() {
    val pastValues = mutableSetOf<T>()
    var numAttempts = 0

    override fun computeNext() {
        while (source.hasNext()) {
            val currentElement = source.next()
            if (pastValues.add(currentElement)) {
                setNext(currentElement)
                numAttempts = 0
                return
            } else if (++numAttempts >= maxConsecutiveAttempts) break
        }
        done()
    }
}
