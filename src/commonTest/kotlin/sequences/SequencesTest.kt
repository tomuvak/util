package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.*
import kotlin.test.*

class SequencesTest {
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
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNullIfNone {
            testedElements.add(it)
            false
        }}, ::assertNull)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNullIfNoneReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNullIfNone {
            testedElements.add(it)
            it == 3
        }}) { assertEquals(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
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
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({
            assertFailsWith<NoSuchElementException> { singleOrNullIfMultiple {
                testedElements.add(it)
                false
            } }
        })
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNullIfMultipleWithPredicateReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNullIfMultiple {
            testedElements.add(it)
            it == 3
        } }) { assertEquals(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
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
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNullIfNoneOrMultiple {
            testedElements.add(it)
            false
        } }, ::assertNull)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNullIfNoneOrMultipleReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNullIfNoneOrMultiple {
            testedElements.add(it)
            it == 3
        } }) { assertEquals(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
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
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ assertFailsWith<NoSuchElementException> { singleNoVerify {
            testedElements.add(it)
            false
        } } })
        assertEquals(listOf(1, 2, 3), testedElements)
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
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ singleNoVerifyOrNull {
            testedElements.add(it)
            false
        } }, ::assertNull)
        assertEquals(listOf(1, 2, 3), testedElements)
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
            override fun hasNext(): Boolean = fail("Not supposed to actually be called")
            override fun next(): Int = fail("Not supposed to actually be called")
        }
        assertSame(iterator, assertIs<CachingSequence<Int>>(iterator.asSequence().cached()).iterator)
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
}
