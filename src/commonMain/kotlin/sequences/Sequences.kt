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
 * Yields all elements of the receiver sequence [this] before the first one which is not of type [T] (if any; yields all
 * elements if they're all of type T). This operation is _intermediate_ and _stateless_.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> Sequence<Any?>.takeWhileIsInstance(): Sequence<T> = takeWhile { it is T } as Sequence<T>
