package com.tomuvak.util.sequences

/**
 * Returns a list of sequences, each of which is the result of running the corresponding transform (of the given
 * [transforms]) on a sequence equivalent to (that is comprising the same elements as) the receiver sequence [this].
 * Iterating over (any or all of) the returned sequences in full or in part will iterate the original sequence [this] at
 * most once, and at no point further than is required in order to yield the elements (of the returned sequences)
 * iterated over until that point.
 *
 * None of the given [transforms] is allowed to iterate its input sequence more than once. For typical use cases that
 * also implies none of the returned sequences can be iterated multiple times, though that really depends on the
 * transforms themselves: a transform is allowed to return any sequence, not each iteration of which necessarily
 * requiring (re)iteration of the original sequence (just as one example, the transform may cache its input and/or
 * output).
 *
 * This operation is _intermediate_ and _stateful_.
 */
fun <T, R> Sequence<T>.transform(transforms: List<(Sequence<T>) -> Sequence<R>>): List<Sequence<R>> =
    MultiTransformSequences(this, transforms).transformedSequences

private class MultiTransformSequences<T, R>(source: Sequence<T>, transforms: List<(Sequence<T>) -> Sequence<R>>) {
    private val cached = source.cached(transforms.size)
    val transformedSequences: List<Sequence<R>> = transforms.map { it(cached.constrainOnce()) }
}
