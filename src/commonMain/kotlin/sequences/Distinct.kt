package com.tomuvak.util.sequences

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
fun <T> Sequence<T>.distinct(maxConsecutiveAttempts: Int): Sequence<T> = distinctBy(maxConsecutiveAttempts) { it }

/**
 * Returns a sequence which yields the exact same elements as the receiver sequence [this], in the same order, but
 * without multiple elements having the same key as determined by the given [keySelector] – that is elements whose keys
 * have already appeared before are filtered out, and also stopping as soon as [maxConsecutiveAttempts] (which has to be
 * positive) consecutive elements have been filtered out this way, if ever.
 *
 * This is similar to the standard library's `distinctBy` extension function, but with the added functionality of the
 * returned sequence stopping when [maxConsecutiveAttempts] consecutive elements have keys which are repetitions of keys
 * of past elements. One case where this is useful is when the receiver sequence [this] is potentially infinite but
 * containing elements with only a finite number of _distinct_ keys: in that case the standard library's `.distinctBy()`
 * will return a sequence which enters an infinite loop (after retrieving elements with all unique keys, trying to
 * retrieve the next element will not fail – it will simply never return), whereas the sequence returned by this
 * function will stop (note that this does **not** mean that the returned sequence can never be infinite: it will be if
 * the original sequence is itself infinite and _keeps yielding elements with **distinct** keys_). Of course, this
 * feature also means that it's possible for the returned sequence _not_ to contain elements with all distinct keys of
 * elements from the original sequence: any element with a new key which follows more than [maxConsecutiveAttempts]
 * consecutive elements with old keys will be missed.
 *
 * This operation is _intermediate_ but _stateful_.
 */
fun <T, K> Sequence<T>.distinctBy(maxConsecutiveAttempts: Int, keySelector: (T) -> K): Sequence<T> {
    require(maxConsecutiveAttempts > 0) { "maxConsecutiveAttempts must be positive (was $maxConsecutiveAttempts)" }
    return DistinctSequence(this, maxConsecutiveAttempts, keySelector)
}

private class DistinctSequence<T, K>(
    private val source: Sequence<T>,
    private val maxConsecutiveAttempts: Int,
    private val keySelector: (T) -> K
): Sequence<T> {
    override fun iterator(): Iterator<T> = DistinctIterator(source.iterator(), maxConsecutiveAttempts, keySelector)
}

private class DistinctIterator<T, K>(
    private val source: Iterator<T>,
    private val maxConsecutiveAttempts: Int,
    private val keySelector: (T) -> K
): AbstractIterator<T>() {
    val pastValues = mutableSetOf<K>()
    var numAttempts = 0

    override fun computeNext() {
        while (source.hasNext()) {
            val currentElement = source.next()
            if (pastValues.add(keySelector(currentElement))) {
                setNext(currentElement)
                numAttempts = 0
                return
            } else if (++numAttempts >= maxConsecutiveAttempts) break
        }
        done()
    }
}
