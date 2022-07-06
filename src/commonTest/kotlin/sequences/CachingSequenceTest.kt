package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertStartsWith
import com.tomuvak.testing.assertions.assertValues
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals

class CachingSequenceTest {
    @Test fun cachingSequenceHasSameElements() {
        CachingSequence(emptySequence<Int>()).assertValues()
        CachingSequence(sequenceOf(1)).assertValues(1)
        CachingSequence(sequenceOf(1, 2)).assertValues(1, 2)
        CachingSequence(sequenceOf(1, 2, 3)).assertValues(1, 2, 3)
    }

    @Test fun cachingSequenceYieldsSameElementsOnReiteration() {
        CachingSequence(emptySequence<Int>()).assertReiterableWithSameElements()
        CachingSequence(sequenceOf(1)).assertReiterableWithSameElements()
        CachingSequence(sequenceOf(1, 2)).assertReiterableWithSameElements()
        CachingSequence(sequenceOf(1, 2, 3)).assertReiterableWithSameElements()
    }

    @Test fun cachingSequenceDoesNotReiterateOriginalSequence() {
        CachingSequence(emptySequence<Int>().constrainOnce()).assertReiterableWithSameElements()
        CachingSequence(sequenceOf(1).constrainOnce()).assertReiterableWithSameElements()
        CachingSequence(sequenceOf(1, 2).constrainOnce()).assertReiterableWithSameElements()
        CachingSequence(sequenceOf(1, 2, 3).constrainOnce()).assertReiterableWithSameElements()
    }

    @Test fun cachingSequenceEnumeratesOriginalSequenceLazily() {
        var lastEnumerated = -1
        val cachingSequence = CachingSequence(sequenceOf(0, 1, 2).onEach { lastEnumerated = max(lastEnumerated, it) })
        cachingSequence.assertStartsWith()
        assertEquals(-1, lastEnumerated)
        cachingSequence.assertStartsWith(0)
        assertEquals(0, lastEnumerated)
        cachingSequence.assertStartsWith(0, 1)
        assertEquals(1, lastEnumerated)
        cachingSequence.assertStartsWith(0, 1, 2)
        assertEquals(2, lastEnumerated)
    }

    private inline fun <reified T> Sequence<T>.assertReiterableWithSameElements() =
        assertValues(*toList().toTypedArray())
}
