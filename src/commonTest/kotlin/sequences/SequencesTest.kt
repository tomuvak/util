package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertStartsWith
import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.testIntermediateOperation
import kotlin.test.*

class SequencesTest {
    @Test fun singleOrNullIfEmptyReturnsNullWhenEmpty() =
        assertNull(emptySequence<Int>().constrainOnce().singleOrNullIfEmpty())
    @Test fun singleOrNullIfEmptyReturnsValueWhenSingle() =
        assertEquals(3, sequenceOf(3).constrainOnce().singleOrNullIfEmpty())
    @Test fun singleOrNullIfEmptyThrowsWhenMultiple() {
        assertFailsWith<IllegalArgumentException> { sequenceOf(3, 3).constrainOnce().singleOrNullIfEmpty() }
    }
    @Test fun singleOrNullIfEmptyDoesNotEvaluateFurther() {
        assertFailsWith<IllegalArgumentException> { sequence {
            yield(3)
            yield(3)
            fail("Not supposed to enumerate thus far")
        }.constrainOnce().singleOrNullIfEmpty() }
    }

    @Test fun singleOrNullIfNoneReturnsNullWhenEmpty() =
        assertNull(emptySequence<Int>().constrainOnce().singleOrNullIfNone { fail("Not supposed to be called") })
    @Test fun singleOrNullIfNoneReturnsNullWhenNone() {
        val testedElements = mutableListOf<Int>()
        assertNull(sequenceOf(1, 2, 3).constrainOnce().singleOrNullIfNone {
            testedElements.add(it)
            false
        })
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNullIfNoneReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        assertEquals(
            3,
            sequenceOf(1, 2, 3, 4, 5).constrainOnce().singleOrNullIfNone {
                testedElements.add(it)
                it == 3
            }
        )
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun singleOrNullIfNoneThrowsWhenMultiple() {
        assertFailsWith<IllegalArgumentException> {
            sequence {
                yield(1)
                yield(2)
                yield(3)
                yield(3)
                fail("Not supposed to enumerate thus far")
            }.constrainOnce().singleOrNullIfNone { it == 3 }
        }
    }

    @Test fun singleOrNullIfMultipleThrowsWhenEmpty() {
        assertFailsWith<NoSuchElementException> { emptySequence<Int>().constrainOnce().singleOrNullIfMultiple() }
    }
    @Test fun singleOrNullIfMultipleReturnsValueWhenSingle() =
        assertEquals(3, sequenceOf(3).constrainOnce().singleOrNullIfMultiple())
    @Test fun singleOrNullIfMultipleReturnsNullWhenMultiple() =
        assertNull(sequenceOf(3, 3).constrainOnce().singleOrNullIfMultiple())
    @Test fun singleOrNullIfMultipleDoesNotEvaluateFurther() = assertNull(sequence {
        yield(3)
        yield(3)
        fail("Not supposed to enumerate thus far")
    }.constrainOnce().singleOrNullIfMultiple())

    @Test fun singleOrNullIfMultipleWithPredicateThrowsWhenEmpty() {
        assertFailsWith<NoSuchElementException> {
            emptySequence<Int>().constrainOnce().singleOrNullIfMultiple { fail("Not supposed to be called") }
        }
    }
    @Test fun singleOrNullIfMultipleWithPredicateThrowsWhenNone() {
        val testedElements = mutableListOf<Int>()
        assertFailsWith<NoSuchElementException> {
            sequenceOf(1, 2, 3).constrainOnce().singleOrNullIfMultiple {
                testedElements.add(it)
                false
            }
        }
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNullIfMultipleWithPredicateReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        assertEquals(
            3,
            sequenceOf(1, 2, 3, 4, 5).constrainOnce().singleOrNullIfMultiple {
                testedElements.add(it)
                it == 3
            }
        )
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun singleOrNullIfMultipleWithPredicateReturnsNullWhenMultiple() = assertNull(sequence {
        yield(1)
        yield(2)
        yield(3)
        yield(3)
        fail("Not supposed to enumerate thus far")
    }.constrainOnce().singleOrNullIfMultiple { it == 3 })

    @Test fun singleOrNullIfEmptyOrMultipleReturnsNullWhenEmpty() =
        assertNull(emptySequence<Int>().constrainOnce().singleOrNullIfEmptyOrMultiple())
    @Test fun singleOrNullIfEmptyOrMultipleReturnsValueWhenSingle() =
        assertEquals(3, sequenceOf(3).constrainOnce().singleOrNullIfEmptyOrMultiple())
    @Test fun singleOrNullIfEmptyOrMultipleReturnsNullWhenMultiple() =
        assertNull(sequenceOf(3, 3).constrainOnce().singleOrNullIfEmptyOrMultiple())
    @Test fun singleOrNullIfEmptyOrMultipleDoesNotEvaluateFurther() = assertNull(sequence {
        yield(3)
        yield(3)
        fail("Not supposed to enumerate thus far")
    }.constrainOnce().singleOrNullIfEmptyOrMultiple())

    @Test fun singleOrNullIfNoneOrMultipleReturnsNullWhenEmpty() = assertNull(
        emptySequence<Int>().constrainOnce().singleOrNullIfNoneOrMultiple { fail("Not supposed to be called") }
    )
    @Test fun singleOrNullIfNoneOrMultipleReturnsNullWhenNone() {
        val testedElements = mutableListOf<Int>()
        assertNull(sequenceOf(1, 2, 3).constrainOnce().singleOrNullIfNoneOrMultiple {
            testedElements.add(it)
            false
        })
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNullIfNoneOrMultipleReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        assertEquals(
            3,
            sequenceOf(1, 2, 3, 4, 5).constrainOnce().singleOrNullIfNoneOrMultiple {
                testedElements.add(it)
                it == 3
            }
        )
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun singleOrNullIfNoneOrMultipleReturnsNullWhenMultiple() = assertNull(sequence {
        yield(1)
        yield(2)
        yield(3)
        yield(3)
        fail("Not supposed to enumerate thus far")
    }.constrainOnce().singleOrNullIfNoneOrMultiple { it == 3 })

    @Test fun singleNoVerifyThrowsWhenEmpty() {
        assertFailsWith<NoSuchElementException> { emptySequence<Int>().constrainOnce().singleNoVerify() }
    }
    @Test fun singleNoVerifyReturnsSingle() = assertEquals(3, sequenceOf(3).constrainOnce().singleNoVerify())
    @Test fun singleNoVerifyDoesNotCheckFurther() = assertEquals(
        3,
        sequence {
            yield(3)
            fail("Not supposed to enumerate thus far")
        }.constrainOnce().singleNoVerify()
    )

    @Test fun singleNoVerifyWithPredicateWhenEmpty() {
        assertFailsWith<NoSuchElementException> { emptySequence<Int>().constrainOnce().singleNoVerify {
            fail("Not supposed to be called")
        } }
    }
    @Test fun singleNoVerifyWithPredicateWithNoMatchingElement() {
        val testedElements = mutableListOf<Int>()
        assertFailsWith<NoSuchElementException> { sequenceOf(1, 2, 3).constrainOnce().singleNoVerify {
            testedElements.add(it)
            false
        } }
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleNoVerifyWithPredicateReturnsMatchingElement() {
        val testedElements = mutableListOf<Int>()
        assertEquals(
            3,
            sequenceOf(1, 2, 3, 4, 5).constrainOnce().singleNoVerify {
                testedElements.add(it)
                it == 3
            }
        )
        assertEquals(listOf(1, 2, 3), testedElements)
    }

    @Test fun singleNoVerifyOrNullReturnsNullWhenEmpty() =
        assertNull(emptySequence<Int>().constrainOnce().singleNoVerifyOrNull())
    @Test fun singleNoVerifyOrNullReturnsSingle() =
        assertEquals(3, sequenceOf(3).constrainOnce().singleNoVerifyOrNull())
    @Test fun singleNoVerifyOrNullDoesNotCheckFurther() = assertEquals(
        3,
        sequence {
            yield(3)
            fail("Not supposed to enumerate thus far")
        }.constrainOnce().singleNoVerifyOrNull()
    )

    @Test fun singleNoVerifyOrNullWithPredicateWhenEmpty() = assertNull(
        emptySequence<Int>().constrainOnce().singleNoVerifyOrNull { fail("Not supposed to be called") }
    )
    @Test fun singleNoVerifyOrNullWithPredicateWithNoMatchingElement() {
        val testedElements = mutableListOf<Int>()
        assertNull(sequenceOf(1, 2, 3).constrainOnce().singleNoVerifyOrNull {
            testedElements.add(it)
            false
        })
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleNoVerifyOrNullWithPredicateReturnsMatchingElement() {
        val testedElements = mutableListOf<Int>()
        assertEquals(
            3,
            sequenceOf(1, 2, 3, 4, 5).constrainOnce().singleNoVerifyOrNull {
                testedElements.add(it)
                it == 3
            }
        )
        assertEquals(listOf(1, 2, 3), testedElements)
    }

    @Test fun ifNotEmptyReturnsNullWhenEmpty() = assertNull(emptySequence<Int>().constrainOnce().ifNotEmpty())
    @Test fun ifNotEmptyReturnsEquivalentSequence() = sequenceOf(1, 2, 3).testIntermediateOperation({
        assertNotNull(ifNotEmpty())
    }) { assertValues(1, 2, 3) }
    @Test fun ifNotEmptyEnumeratesOriginalSequenceLazily() = sequence {
        yield(1)
        fail("Should not enumerate this far")
    }.testIntermediateOperation({ assertNotNull(ifNotEmpty()) }) { assertStartsWith(1) }

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
        sequence<Number> {
            fail("Not supposed to be enumerated this far")
        }.testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertStartsWith() }
        sequence<Number> {
            yield(1)
            fail("Not supposed to be enumerated this far")
        }.testIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertStartsWith(1) }
    }
}
