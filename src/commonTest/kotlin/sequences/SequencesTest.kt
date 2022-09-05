package com.tomuvak.util.sequences

import com.tomuvak.testing.*
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
        fun test(vararg elements: Number, continuation: Sequence<Int>.() -> Unit) =
            elements.asSequence().testIntermediateOperation({ takeWhileIsInstance<Int>() }) { continuation() }

        test() { assertValues() }
        test(1) { assertValues(1) }
        test(1L) { assertValues() }
        test(1, 2) { assertValues(1, 2) }
        test(1, 2L) { assertValues(1) }
        test(1L, 2) { assertValues() }
        test(1L, 2L) { assertValues() }
    }
    @Test fun takeWhileIsInstanceEnumeratesSequenceLazily() {
        emptySequence<Number>().testLazyIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertStartsWith() }
        sequenceOf<Number>(1).testLazyIntermediateOperation({ takeWhileIsInstance<Int>() }) { assertStartsWith(1) }
    }
}
