package com.tomuvak.util.sequences

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

fun <T> Sequence<T>.cached(): Sequence<T> = CachingSequence(this)

@Suppress("UNCHECKED_CAST") inline fun <reified T> Sequence<*>.takeWhileIsInstance(): Sequence<T> =
    takeWhile { it is T } as Sequence<T>