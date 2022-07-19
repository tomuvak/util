package com.tomuvak.util.sequences

/**
 * Returns a sequence which yields the exact same elements as the receiver sequence [this] and is iterable multiple
 * times – even if the original sequence is not. The original sequence is guaranteed not to be iterated over more than
 * once, and, at any point during the first (and only) iteration, not to be iterated over further than is necessary.
 *
 * The behavior of the returned sequence in case iterating the original sequence throws an error at any point is
 * undefined.
 *
 * The elements of the original sequence are accessible as long as the returned sequence (or an iterator thereof) is
 * alive. To allow them to be garbage-collected, make sure not to keep references to the returned sequence once it's no
 * longer needed.
 *
 * This operation is _intermediate_ and _stateful_.
 */
fun <T> Sequence<T>.cached(): Sequence<T> = if (this is CachingSequence<T>) this else CachingSequence(this)

/**
 * Returns a sequence which yields the exact same elements as the receiver sequence [this] and is iterable up to
 * [maxIterations] (which has to be positive) times – potentially in parallel – even if the original sequence is not.
 * The original sequence is guaranteed not to be iterated over more than once, and, at any point during the first (and
 * only) iteration, not to be iterated over further than has been required by the most advanced of the (at most
 * [maxIterations]) iterations of the returned sequence.
 *
 * The behavior of the returned sequence in case iterating the original sequence throws an error at any point is
 * undefined.
 *
 * Each of the elements of the original sequence is accessible as long as it has not been iterated over [maxIterations]
 * times through the returned sequence. To allow them to be garbage-collected, make sure to iterate the returned
 * sequence completely [maxIterations] times, or otherwise not to keep references to it once it's no longer needed.
 *
 * This operation is _intermediate_ and _stateful_.
 */
fun <T> Sequence<T>.cached(maxIterations: Int): Sequence<T> {
    require(maxIterations > 0) { "maxIterations must be positive (was $maxIterations)" }
    return ConstrainedCachingSequence(this, maxIterations)
}

private class CachingSequence<T>(original: Sequence<T>): Sequence<T> {
    private val iterator: Iterator<T> by lazy { original.iterator() }
    private val cache: MutableList<T> = mutableListOf()

    private inner class CachingSequenceIterator: Iterator<T> {
        private var index = 0
        override fun hasNext(): Boolean = index < cache.size || iterator.hasNext()
        override fun next(): T = if (index++ < cache.size) cache[index - 1] else iterator.next().also(cache::add)
    }

    override fun iterator(): Iterator<T> = CachingSequenceIterator()
}

internal class ConstrainedCachingSequence<T>(original: Sequence<T>, private val maxIterations: Int): Sequence<T> {
    private val iterator: Iterator<T> by lazy { original.iterator() }
    private var numBygoneElements = 0
    internal val cache: MutableList<T> = mutableListOf()
    private var numYieldedIterators: Int = 0
    private var iterators = List(maxIterations) { CachingSequenceIterator() }

    private inner class CachingSequenceIterator: Iterator<T> {
        private var index = 0
        override fun hasNext(): Boolean = index < numBygoneElements + cache.size || iterator.hasNext()
        override fun next(): T {
            val actualIndex = index++ - numBygoneElements
            if (actualIndex >= cache.size) cache.add(iterator.next())
            val ret = cache[actualIndex]
            if (actualIndex == 0 && iterators.all { it.index >= index }) {
                cache.removeAt(0)
                numBygoneElements++
            }
            return ret
        }
    }

    override fun iterator(): Iterator<T> =
        if (numYieldedIterators < maxIterations) iterators[numYieldedIterators++]
        else throw IllegalStateException("Constrained cached sequence can only be iterated $maxIterations times")
}
