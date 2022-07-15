package com.tomuvak.util.sequences

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
