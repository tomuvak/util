package com.tomuvak.util.sequences

import com.tomuvak.testing.*
import kotlin.test.Test
import kotlin.test.assertFalse

class DistinctTest {
    @Test fun distinctRequiresPositiveArgument() {
        for (i in -3..0)
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("positive", i) {
                Sequence<Int>(mootProvider).distinct(i)
            }
    }
    @Test fun distinctIsIndeedDistinct() =
        sequenceOf(0, 0, 1, 0, 1, 2, 0, 1, 2, 3, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4).testIntermediateOperation({
            distinct(10)
        }) { assertValues(0, 1, 2, 3, 4) }
    @Test fun distinctIsLazy() = sequenceOf(0, 0, 1, 0, 1, 2).testLazyIntermediateOperation({ distinct(10) }) {
        assertStartsWith(0, 1, 2)
    }
    @Test fun distinctStopsAfterMaxConsecutiveAttempts() {
        sequenceOf(0, 0, 1, 0, 1, 2, 0, 1, 2, 3).testIntermediateOperation({ distinct(4) }) { assertValues(0, 1, 2, 3)}
        sequenceOf(0, 0, 1, 0, 1, 2, 0, 1, 2).testLazyIntermediateOperation({ distinct(3) }) { assertValues(0, 1, 2)}
    }
    @Test fun distinctIterationCanBeRequeriedForExhaustionAfterExhaustion() {
        val iterator = emptySequence<Int>().distinct(10).iterator()
        assertFalse(iterator.hasNext())
        assertFalse(iterator.hasNext())
    }

    @Test fun distinctByRequiresPositiveArgument() {
        for (i in -3..0)
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("positive", i) {
                Sequence<Int>(mootProvider).distinctBy(i, mootFunction)
            }
    }
    @Test fun distinctByIsIndeedDistinct() =
        sequenceOf(0, 1, 2, 0, 3, 4, 1, 2, 5, 6, 0, 3, 4, 7, 8, 1, 2, 5, 6, 9).testIntermediateOperation({
            distinctBy(10) { it / 2 }
        }) { assertValues(0, 2, 4, 6, 8) }
    @Test fun distinctByIsLazy() = sequenceOf(0, 1, 2, 0, 3, 4).testLazyIntermediateOperation({
        distinctBy(10) { it / 2 }
    }) { assertStartsWith(0, 2, 4) }
    @Test fun distinctByStopsAfterMaxConsecutiveAttempts() {
        sequenceOf(0, 1, 2, 0, 3, 4, 1, 2, 5, 6).testIntermediateOperation({
            distinctBy(4) { it / 2 }
        }) { assertValues(0, 2, 4, 6)}
        sequenceOf(0, 1, 2, 0, 3, 4, 1, 2, 5).testLazyIntermediateOperation({
            distinctBy(3) { it / 2 }
        }) { assertValues(0, 2, 4)}
    }
    @Test fun distinctByIterationCanBeRequeriedForExhaustionAfterExhaustion() {
        val iterator = emptySequence<Int>().distinctBy(10) { it / 2 }.iterator()
        assertFalse(iterator.hasNext())
        assertFalse(iterator.hasNext())
    }
}
