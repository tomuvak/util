package com.tomuvak.util.sequences

class CachingSequence<T>(original: Sequence<T>): Sequence<T> {
    internal val iterator: Iterator<T> = original.iterator()
    private val cache: MutableList<T> = mutableListOf()

    private inner class CachingSequenceIterator: Iterator<T> {
        private var index = 0
        override fun hasNext(): Boolean = index < cache.size || iterator.hasNext()
        override fun next(): T = if (index++ < cache.size) cache[index - 1] else iterator.next().also(cache::add)
    }

    override fun iterator(): Iterator<T> = CachingSequenceIterator()
}
