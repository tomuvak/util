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
