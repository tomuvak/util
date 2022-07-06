package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertStartsWith
import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.testIntermediateOperation
import kotlin.test.*

class SequencesTest {
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
