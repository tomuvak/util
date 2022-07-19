package com.tomuvak.util.sequences

/**
 * A sequence which yields the exact same elements as the given [original] sequence and is iterable multiple times –
 * even if the original sequence is not. The original sequence is guaranteed not to be iterated over more than once,
 * and, during the first (and only) iteration, not to be iterated over further than is necessary.
 */
class CachingSequence<T>(original: Sequence<T>): Sequence<T> {
    internal val iterator: Iterator<T> by lazy { original.iterator() }
    private val cache: MutableList<T> = mutableListOf()

    private inner class CachingSequenceIterator: Iterator<T> {
        private var index = 0
        override fun hasNext(): Boolean = index < cache.size || iterator.hasNext()
        override fun next(): T = if (index++ < cache.size) cache[index - 1] else iterator.next().also(cache::add)
    }

    override fun iterator(): Iterator<T> = CachingSequenceIterator()
}

/**
 * Returns a sequence which yields the exact same elements as the receiver sequence [this] and is iterable multiple
 * times – even if the original sequence is not. The original sequence is guaranteed not to be iterated over more than
 * once, and, during the first (and only) iteration, not to be iterated over further than is necessary (that is this
 * operation, while not _stateless_, is _intermediate_).
 */
fun <T> Sequence<T>.cached(): Sequence<T> = if (this is CachingSequence<T>) this else CachingSequence(this)
