package com.tomuvak.util.sequences

import com.tomuvak.testing.assertFailsWithTypeAndMessageContaining
import com.tomuvak.testing.assertStartsWith
import com.tomuvak.testing.assertValues
import com.tomuvak.testing.mootProvider
import com.tomuvak.util.nextOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class CachingTest {
    @Test fun cachedSequenceHasSameElements() {
        emptySequence<Int>().cached().assertValues()
        sequenceOf(1).cached().assertValues(1)
        sequenceOf(1, 2).cached().assertValues(1, 2)
        sequenceOf(1, 2, 3).cached().assertValues(1, 2, 3)
    }
    @Test fun cachedSequenceYieldsSameElementsOnReiteration() {
        emptySequence<Int>().cached().assertReiterableWithSameElements(4)
        sequenceOf(1).cached().assertReiterableWithSameElements(4)
        sequenceOf(1, 2).cached().assertReiterableWithSameElements(4)
        sequenceOf(1, 2, 3).cached().assertReiterableWithSameElements(4)
    }
    @Test fun cachedSequenceDoesNotReiterateOriginalSequence() {
        emptySequence<Int>().constrainOnce().cached().assertReiterableWithSameElements(4)
        sequenceOf(1).constrainOnce().cached().assertReiterableWithSameElements(4)
        sequenceOf(1, 2).constrainOnce().cached().assertReiterableWithSameElements(4)
        sequenceOf(1, 2, 3).constrainOnce().cached().assertReiterableWithSameElements(4)
    }
    @Test fun cachedSequenceDoesNotTryToIterateOriginalSequenceBeforeItHasTo() { Sequence<Int>(mootProvider).cached() }
    @Test fun cachedSequenceEnumeratesOriginalSequenceLazily() {
        var lastEnumerated = -1
        val cachedSequence = sequenceOf(0, 1, 2).onEach { lastEnumerated = it }.cached()
        cachedSequence.assertStartsWith()
        assertEquals(-1, lastEnumerated)
        cachedSequence.assertStartsWith(0)
        assertEquals(0, lastEnumerated)
        cachedSequence.assertStartsWith(0, 1)
        assertEquals(1, lastEnumerated)
        cachedSequence.assertStartsWith(0, 1, 2)
        assertEquals(2, lastEnumerated)
    }
    @Test fun cachedSequenceIsOriginalSequenceWhenAlreadyCached() {
        val sequence = Sequence<Int>(mootProvider).cached()
        assertSame(sequence, sequence.cached())
    }

    @Test fun cachedRequiresPositiveArgument() {
        for (i in -3..0)
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("positive", i) {
                Sequence<Int>(mootProvider).cached(i)
            }
    }
    @Test fun constrainedCachedSequenceHasSameElements() {
        emptySequence<Int>().cached(3).assertValues()
        sequenceOf(1).cached(3).assertValues(1)
        sequenceOf(1, 2).cached(3).assertValues(1, 2)
        sequenceOf(1, 2, 3).cached(3).assertValues(1, 2, 3)
    }
    @Test fun constrainedCachedSequenceYieldsSameElementsOnReiteration() {
        emptySequence<Int>().cached(3).assertReiterableWithSameElements(3)
        sequenceOf(1).cached(3).assertReiterableWithSameElements(3)
        sequenceOf(1, 2).cached(3).assertReiterableWithSameElements(3)
        sequenceOf(1, 2, 3).cached(3).assertReiterableWithSameElements(3)
    }
    @Test fun constrainedCachedSequenceCannotBeIteratedMoreThanMaxIterations() {
        val cached = sequenceOf(1, 2, 3).cached(5)
        repeat(5) { cached.iterator() }
        assertFailsWithTypeAndMessageContaining<IllegalStateException>(5) { cached.iterator() }
    }
    @Test fun constrainedCachedSequenceDoesNotReiterateOriginalSequence() {
        emptySequence<Int>().constrainOnce().cached(3).assertReiterableWithSameElements(3)
        sequenceOf(1).constrainOnce().cached(3).assertReiterableWithSameElements(3)
        sequenceOf(1, 2).constrainOnce().cached(3).assertReiterableWithSameElements(3)
        sequenceOf(1, 2, 3).constrainOnce().cached(3).assertReiterableWithSameElements(3)
    }
    @Test fun constrainedCachedSequenceDoesNotTryToIterateOriginalSequenceBeforeItHasTo() {
        Sequence<Int>(mootProvider).cached(3)
    }
    @Test fun constrainedCachedSequenceEnumeratesOriginalSequenceLazily() {
        var lastEnumerated = -1
        val cached = sequenceOf(0, 1, 2).onEach { lastEnumerated = it }.cached(4)
        cached.assertStartsWith()
        assertEquals(-1, lastEnumerated)
        cached.assertStartsWith(0)
        assertEquals(0, lastEnumerated)
        cached.assertStartsWith(0, 1)
        assertEquals(1, lastEnumerated)
        cached.assertStartsWith(0, 1, 2)
        assertEquals(2, lastEnumerated)
    }
    @Test fun constrainedCachedSequenceForgetsElementsWhenNoLongerNeeded() {
        val cached = sequenceOf(0, 1, 2, 3).cached(6) as ConstrainedCachingSequence<Int>
        assertEquals(0, cached.cache.size)
        cached.assertReiterableWithSameElements(5)
        assertEquals(4, cached.cache.size)

        val iterator = cached.iterator()
        assertEquals(0, iterator.next())
        assertEquals(3, cached.cache.size)
        assertEquals(1, iterator.next())
        assertEquals(2, cached.cache.size)
        assertEquals(2, iterator.next())
        assertEquals(1, cached.cache.size)
        assertEquals(3, iterator.next())
        assertEquals(0, cached.cache.size)
    }
    @Test fun constrainedCachedSequenceBehavesCorrectlyDuringParallelIteration() {
        var lastEnumerated = -1
        val cached =
            sequenceOf(0, 1, 2, 3, 4).onEach { lastEnumerated = it }.cached(4) as ConstrainedCachingSequence<Int>
        val iterators = List(4) { cached.iterator() }

        assertEquals(-1, lastEnumerated)
        assertEquals(0, cached.cache.size)

        fun verify(iteratorIndex: Int, expectedNextValue: Int?, expectedLastEnumerated: Int, expectedCacheSize: Int) {
            assertEquals(expectedNextValue, iterators[iteratorIndex].nextOrNull())
            assertEquals(expectedLastEnumerated, lastEnumerated)
            assertEquals(cached.cache.size, expectedCacheSize)
        }

        verify(0, 0, 0, 1)
        verify(1, 0, 0, 1)
        verify(2, 0, 0, 1)

        verify(2, 1, 1, 2)
        verify(3, 0, 1, 1)
        verify(0, 1, 1, 1)

        verify(0, 2, 2, 2)

        verify(0, 3, 3, 3)
        verify(1, 1, 3, 3)
        verify(1, 2, 3, 3)
        verify(2, 2, 3, 3)
        verify(2, 3, 3, 3)

        verify(2, 4, 4, 4)
        verify(3, 1, 4, 3)
        verify(3, 2, 4, 2)
        verify(3, 3, 4, 2)
        verify(3, 4, 4, 2)

        verify(3, null, 4, 2)
        verify(0, 4, 4, 2)
        verify(1, 3, 4, 1)
        verify(2, null, 4, 1)
        verify(0, null, 4, 1)
        verify(1, 4, 4, 0)
        verify(1, null, 4, 0)
    }

    private inline fun <reified T> Sequence<T>.assertReiterableWithSameElements(numIterations: Int) {
        val valuesFromFirstIteration = toList().toTypedArray()
        repeat(numIterations - 1) { assertValues(*valuesFromFirstIteration) }
    }
}
