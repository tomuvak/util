package com.tomuvak.util

import com.tomuvak.testing.gc.tryToAchieveByForcingGc
import kotlinx.coroutines.coroutineScope
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Retrieve the next element from the receiver iterator [this] – and discard it. Useful for tests relying on garbage
 * collection, as using this function – as opposed to calling `.next()` directly – helps them avoid creating a (hidden)
 * strong reference to the element (which would foil garbage collection).
 */
internal fun <T> Iterator<T>.dismissNext() { next() }

/**
 * Returns a pair of a sequence of [numElements] objects and a list of weak references to these objects. Note that while
 * the list of weak references is exposed to calling code as immutable, it in fact gets populated by the references one
 * by one as the sequence is being iterated. The sequence can only be iterated once.
 */
internal fun generateSequenceAndWeakReferences(numElements: Int): Pair<Sequence<Any>, List<WeakReference<Any>>> {
    val references = mutableListOf<WeakReference<Any>>()
    return Pair(
        sequence {
            repeat(numElements) { this.yield(Any().let { it to WeakReference(it) }) }
        }.map { (element, reference) ->
            assertSame(element, assertNotNull(reference.targetOrNull))
            references.add(reference)
            element
        }.constrainOnce(),
        references
    )
}

internal suspend fun WeakReference<Any>.assertTargetOnlyReclaimableAfter(block: () -> Unit) {
    assertTargetNotReclaimable()
    block()
    assertTargetReclaimable()
}
internal suspend fun WeakReference<Any>.assertTargetNotReclaimable() = assertFalse(targetIsReclaimable())
internal suspend fun WeakReference<Any>.assertTargetReclaimable() = assertTrue(targetIsReclaimable())
private suspend fun WeakReference<Any>.targetIsReclaimable(): Boolean =
    coroutineScope { tryToAchieveByForcingGc { targetOrNull == null } }
