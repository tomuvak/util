package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SequencesTest {
    @Test fun ifNotEmptyReturnsNullWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ ifNotEmpty() }, ::assertNull)
    @Test fun ifNotEmptyReturnsEquivalentSequence() = sequenceOf(1, 2, 3).testIntermediateOperation({
        assertNotNull(ifNotEmpty())
    }) { assertValues(1, 2, 3) }
    @Test fun ifNotEmptyEnumeratesOriginalSequenceLazily() = sequenceOf(1).testLazyIntermediateOperation({
        assertNotNull(ifNotEmpty())
    }) { assertStartsWith(1) }

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
