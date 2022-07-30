package com.tomuvak.util.sequences

/**
 * Returns a sequence containing all elements of the receiver sequence [this] except for the last [n] (which is not
 * allowed to be negative) ones (resulting in an empty sequence in case there are only [n] elements or less to begin
 * with).
 *
 * The resulting sequence has the same elements as the result of calling the standard library's
 * `.toList().dropLast(`[n]`)`, but is a sequence rather than a list, and it consumes the original sequence lazily
 * (though note it will always have consumed the [n] elements following any element it yields. In particular, when the
 * result is enumerated completely, the entire original sequence will have been enumerated as well, including the
 * dropped elements).
 *
 * For a given [n], any sequence is the concatenation of its [dropLast]`(n)` and its [takeLast]`(n)`.
 *
 * This operation is _intermediate_ and _stateful_.
 */
fun <T> Sequence<T>.dropLast(n: Int): Sequence<T> {
    require(n >= 0) { "Can't drop a negative number of elements ($n)" }
    return if (n == 0) this else Sequence { DropLastIterator(iterator(), n) }
}

private class DropLastIterator<T>(private val sourceIterator: Iterator<T>, private val n: Int): Iterator<T> {
    private var index: Int = -1
    private var buffer: Array<Any?>? = null

    private fun ensureInitialized() {
        if (index == -1) {
            index = 0
            val firstElements = arrayOfNulls<Any?>(n)
            buffer = firstElements
            for (i in 0 until n)
                if (sourceIterator.hasNext()) firstElements[i] = sourceIterator.next()
                else {
                    buffer = null
                    break
                }
        }
    }

    override fun hasNext(): Boolean {
        ensureInitialized()
        if (buffer == null) return false
        if (sourceIterator.hasNext()) return true
        buffer = null
        return false
    }

    override fun next(): T {
        ensureInitialized()
        if (buffer == null) throw NoSuchElementException()
        val nextSourceElement = try { sourceIterator.next() } catch (e: Throwable) {
            buffer = null
            throw e
        }
        val buffer = buffer!!
        @Suppress("UNCHECKED_CAST") return buffer[index].also {
            buffer[index++] = nextSourceElement
            index %= buffer.size
        } as T
    }
}

/**
 * Returns a sequence containing all elements of the receiver sequence [this] except for the longest suffix all of whose
 * elements satisfy the given [predicate] (in the extreme cases this can result in the whole original sequence in case
 * the last element does not satisfy the predicate, or in an empty sequence in case all elements satisfy it).
 *
 * The resulting sequence has the same elements as the result of calling the standard library's
 * `.toList().dropLastWhile(`[predicate]`)`, but is a sequence rather than a list, and it consumes the original sequence
 * lazily (though note that when it yields an element which satisfies the predicate, it will always have consumed the
 * elements immediately following it up to and including the first one which doesn't, and when the result is enumerated
 * completely, the entire original sequence will have been enumerated as well, including any dropped elements). Whether
 * that's preferable to calling `.toList().dropLastWhile(predicate)` for a particular use case or not depends on several
 * factors; this function enumerates the original sequence lazily, and does not hold more elements in memory at any
 * given time than it has to, but it computes the given [predicate] for each and every element, in iteration order,
 * whereas the version for lists computes the predicate for elements going from the end backwards, and stops at the
 * first (= last) element which does not satisfy it.
 *
 * For a given [predicate], any sequence is the concatenation of its [dropLastWhile]`(predicate)` and its
 * [takeLastWhile]`(predicate)`.
 *
 * This operation is _intermediate_ and _stateful_.
 */
fun <T> Sequence<T>.dropLastWhile(predicate: (T) -> Boolean): Sequence<T> =
    Sequence { DropLastWhileIterator(iterator(), predicate) }

private class DropLastWhileIterator<T>(
    private val sourceIterator: Iterator<T>,
    private val predicate: (T) -> Boolean
) : Iterator<T> {
    private var buffer: ArrayDeque<T>? = ArrayDeque()

    private fun checkNext() {
        val buffer = buffer
        if (buffer == null || buffer.isNotEmpty()) return
        while (sourceIterator.hasNext()) {
            val next = sourceIterator.next().also(buffer::addLast)
            if (!predicate(next)) return
        }
        this.buffer = null
    }

    override fun hasNext(): Boolean {
        checkNext()
        return buffer != null
    }

    override fun next(): T {
        checkNext()
        val buffer = buffer
        return if (buffer == null) throw NoSuchElementException() else buffer.removeFirst()
    }
}

/**
 * Returns a list of the last [n] (which is not allowed to be negative) elements of the receiver sequence [this]
 * (resulting in a list of all elements in case there are only [n] elements or less to begin with).
 *
 * The result is the same as that of calling the standard library's `.toList().takeLast(`[n]`)`, but the computation is
 * less wasteful, as it never holds more than [n] elements in memory at any one time.
 *
 * For a given [n], any sequence is the concatenation of its [dropLast]`(n)` and its [takeLast]`(n)`.
 *
 * This operation is _terminal_ (as for a general sequence this cannot be computed without iterating over all of the
 * elements. For this reason it also returns the result as a list rather than as a sequence).
 */
fun <T> Sequence<T>.takeLast(n: Int): List<T> {
    require(n >= 0) { "Can't take a negative number of elements ($n)" }
    if (n == 0) return emptyList()

    val iterator = iterator()
    val buffer = ArrayList<T>(n)
    repeat(n) {
        if (!iterator.hasNext()) return@takeLast buffer
        buffer.add(iterator.next())
    }

    var index = 0
    while (iterator.hasNext()) {
        buffer[index++] = iterator.next()
        index %= n
    }
    return if (index == 0) buffer else buffer.subList(index, n) + buffer.subList(0, index)
}

/**
 * Returns the longest suffix of the receiver sequence [this] all of whose elements satisfy the given [predicate] (in
 * the extreme cases this can be empty if the last element does not satisfy the predicate, or the whole original
 * sequence in case all elements satisfy it), as a list.
 *
 * The result is the same as that of calling the standard library's `.toList().takeLastWhile(`[predicate]`)`. Which
 * option is preferable for a particular use case depends on several factors: this function does not hold more elements
 * in memory at any given time than it has to, but it computes the given [predicate] for each and every element, in
 * iteration order, whereas the version for lists computes the predicate for elements going from the end backwards, and
 * stops at the first (= last) element which does not satisfy it.
 *
 * For a given [predicate], any sequence is the concatenation of its [dropLastWhile]`(predicate)` and its
 * [takeLastWhile]`(predicate)`.
 *
 * This operation is _terminal_ (as for a general sequence this cannot be computed without iterating over all of the
 * elements. For this reason it also returns the result as a list rather than as a sequence).
 */
fun <T> Sequence<T>.takeLastWhile(predicate: (T) -> Boolean): List<T> {
    val buffer = mutableListOf<T>()
    for (element in this) if (predicate(element)) buffer.add(element) else buffer.clear()
    return buffer
}
