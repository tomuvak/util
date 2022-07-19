package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertStartsWith
import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.mootProvider
import kotlin.math.max
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
        emptySequence<Int>().cached().assertReiterableWithSameElements()
        sequenceOf(1).cached().assertReiterableWithSameElements()
        sequenceOf(1, 2).cached().assertReiterableWithSameElements()
        sequenceOf(1, 2, 3).cached().assertReiterableWithSameElements()
    }

    @Test fun cachedSequenceDoesNotReiterateOriginalSequence() {
        emptySequence<Int>().constrainOnce().cached().assertReiterableWithSameElements()
        sequenceOf(1).constrainOnce().cached().assertReiterableWithSameElements()
        sequenceOf(1, 2).constrainOnce().cached().assertReiterableWithSameElements()
        sequenceOf(1, 2, 3).constrainOnce().cached().assertReiterableWithSameElements()
    }

    @Test fun cachedSequenceDoesNotTryToIterateOriginalSequenceBeforeItHasTo() { Sequence<Int>(mootProvider).cached() }

    @Test fun cachedSequenceEnumeratesOriginalSequenceLazily() {
        var lastEnumerated = -1
        val cachedSequence = sequenceOf(0, 1, 2).onEach { lastEnumerated = max(lastEnumerated, it) }.cached()
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

    private inline fun <reified T> Sequence<T>.assertReiterableWithSameElements() =
        assertValues(*toList().toTypedArray())
}
