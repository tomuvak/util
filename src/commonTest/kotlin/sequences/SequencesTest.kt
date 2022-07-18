package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.*
import kotlin.test.*

class SequencesTest {
    private val alwaysFalse: MockFunction<Int, Boolean> = MockFunction { false }
    private val isThree: MockFunction<Int, Boolean> = MockFunction { it == 3 }

    @Test fun singleOrNullIfEmptyReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNullIfEmpty() }, ::assertNull)
    @Test fun singleOrNullIfEmptyReturnsValueWhenSingle() =
        sequenceOf(3).testTerminalOperation({ singleOrNullIfEmpty() }) { assertEquals(3, it) }
    @Test fun singleOrNullIfEmptyThrowsWhenMultiple() = sequenceOf(3, 3).testLazyTerminalOperation({
        assertFailsWith<IllegalArgumentException> { singleOrNullIfEmpty() }
    })

    @Test fun singleOrNullIfNoneReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNullIfNone(mootFunction) }, ::assertNull)
    @Test fun singleOrNullIfNoneReturnsNullWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNullIfNone(alwaysFalse::invoke) }, ::assertNull)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleOrNullIfNoneReturnsValueWhenSingle() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNullIfNone(isThree::invoke) }) { assertEquals(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isThree.calls)
    }
    @Test fun singleOrNullIfNoneThrowsWhenMultiple() = sequenceOf(1, 2, 3, 3).testLazyTerminalOperation({
        assertFailsWith<IllegalArgumentException> { singleOrNullIfNone { it == 3 } }
    })

    @Test fun singleOrNullIfMultipleThrowsWhenEmpty() = emptySequence<Int>().testTerminalOperation({
        assertFailsWith<NoSuchElementException> { singleOrNullIfMultiple() }
    })
    @Test fun singleOrNullIfMultipleReturnsValueWhenSingle() =
        sequenceOf(3).testTerminalOperation({ singleOrNullIfMultiple() }) { assertEquals(3, it) }
    @Test fun singleOrNullIfMultipleReturnsNullWhenMultiple() =
        sequenceOf(3, 3).testLazyTerminalOperation({ singleOrNullIfMultiple() }, ::assertNull)

    @Test fun singleOrNullIfMultipleWithPredicateThrowsWhenEmpty() = emptySequence<Int>().testTerminalOperation({
        assertFailsWith<NoSuchElementException> { singleOrNullIfMultiple(mootFunction) }
    })
    @Test fun singleOrNullIfMultipleWithPredicateThrowsWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({
            assertFailsWith<NoSuchElementException> { singleOrNullIfMultiple(alwaysFalse::invoke) }
        })
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleOrNullIfMultipleWithPredicateReturnsValueWhenSingle() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({
            singleOrNullIfMultiple(isThree::invoke)
        }) { assertEquals(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isThree.calls)
    }
    @Test fun singleOrNullIfMultipleWithPredicateReturnsNullWhenMultiple() =
        sequenceOf(1, 2, 3, 3).testLazyTerminalOperation({ singleOrNullIfMultiple { it == 3 } }, ::assertNull)

    @Test fun singleOrNullIfEmptyOrMultipleReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNullIfEmptyOrMultiple() }, ::assertNull)
    @Test fun singleOrNullIfEmptyOrMultipleReturnsValueWhenSingle() =
        sequenceOf(3).testTerminalOperation({ singleOrNullIfEmptyOrMultiple() }) { assertEquals(3, it) }
    @Test fun singleOrNullIfEmptyOrMultipleReturnsNullWhenMultiple() =
        sequenceOf(3, 3).testLazyTerminalOperation({ singleOrNullIfEmptyOrMultiple() }, ::assertNull)

    @Test fun singleOrNullIfNoneOrMultipleReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNullIfNoneOrMultiple(mootFunction) }, ::assertNull)
    @Test fun singleOrNullIfNoneOrMultipleReturnsNullWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNullIfNoneOrMultiple(alwaysFalse::invoke) }, ::assertNull)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleOrNullIfNoneOrMultipleReturnsValueWhenSingle() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({
            singleOrNullIfNoneOrMultiple(isThree::invoke)
        }) { assertEquals(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isThree.calls)
    }
    @Test fun singleOrNullIfNoneOrMultipleReturnsNullWhenMultiple() =
        sequenceOf(1, 2, 3, 3).testLazyTerminalOperation({ singleOrNullIfNoneOrMultiple { it == 3 }}, ::assertNull)

    @Test fun singleNoVerifyThrowsWhenEmpty() = emptySequence<Int>().testTerminalOperation({
        assertFailsWith<NoSuchElementException> { singleNoVerify() }
    })
    @Test fun singleNoVerifyReturnsSingle() =
        sequenceOf(3).testLazyTerminalOperation({ singleNoVerify() }) { assertEquals(3, it) }

    @Test fun singleNoVerifyWithPredicateWhenEmpty() = emptySequence<Int>().testTerminalOperation({
        assertFailsWith<NoSuchElementException> { singleNoVerify(mootFunction) }
    })
    @Test fun singleNoVerifyWithPredicateWithNoMatchingElement() {
        sequenceOf(1, 2, 3).testTerminalOperation({
            assertFailsWith<NoSuchElementException> { singleNoVerify(alwaysFalse::invoke) }
        })
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleNoVerifyWithPredicateReturnsMatchingElement() = sequenceOf(1, 2, 3).testLazyTerminalOperation({
        singleNoVerify { it == 3 }
    }) { assertEquals(3, it) }

    @Test fun singleNoVerifyOrNullReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleNoVerifyOrNull() }, ::assertNull)
    @Test fun singleNoVerifyOrNullReturnsSingle() =
        sequenceOf(3).testLazyTerminalOperation({ singleNoVerifyOrNull() }) { assertEquals(3, it) }

    @Test fun singleNoVerifyOrNullWithPredicateWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleNoVerifyOrNull(mootFunction) }, ::assertNull)
    @Test fun singleNoVerifyOrNullWithPredicateWithNoMatchingElement() {
        sequenceOf(1, 2, 3).testTerminalOperation({ singleNoVerifyOrNull(alwaysFalse::invoke) }, ::assertNull)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleNoVerifyOrNullWithPredicateReturnsMatchingElement() =
        sequenceOf(1, 2, 3).testLazyTerminalOperation({ singleNoVerifyOrNull { it == 3 } }) { assertEquals(3, it) }

    @Test fun ifNotEmptyReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ ifNotEmpty() }, ::assertNull)
    @Test fun ifNotEmptyReturnsEquivalentSequence() = sequenceOf(1, 2, 3).testIntermediateOperation({
        assertNotNull(ifNotEmpty())
    }) { assertValues(1, 2, 3) }
    @Test fun ifNotEmptyEnumeratesOriginalSequenceLazily() = sequenceOf(1).testLazyIntermediateOperation({
        assertNotNull(ifNotEmpty())
    }) { assertStartsWith(1) }

    @Test fun cachedYieldsCachingSequenceForGivenSequence() {
        val iterator = object : Iterator<Int> {
            override fun hasNext(): Boolean = mootProvider()
            override fun next(): Int = mootProvider()
        }
        assertSame(iterator, assertIs<CachingSequence<Int>>(iterator.asSequence().cached()).iterator)
    }
    @Test fun cachedYieldsSameSequenceWhenAlreadyCached() {
        val sequence = CachingSequence(Sequence<Int>(mootProvider))
        assertSame(sequence, sequence.cached())
    }

    @Test fun takeWhileIsInstance() {
        sequenceOf<Number>().testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues() }
        sequenceOf<Number>(1).testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues(1) }
        sequenceOf<Number>(1L).testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues() }
        sequenceOf<Number>(1, 2).testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues(1, 2) }
        sequenceOf<Number>(1, 2L).testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues(1) }
        sequenceOf<Number>(1L, 2).testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues() }
        sequenceOf<Number>(1L, 2L).testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertValues() }
    }

    @Test fun takeWhileIsInstanceEnumeratesSequenceLazily() {
        emptySequence<Number>().testLazyIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertStartsWith() }
        sequenceOf<Number>(1).testLazyIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertStartsWith(1) }
    }

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
}
