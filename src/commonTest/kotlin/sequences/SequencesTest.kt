package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertStartsWith
import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.testIntermediateOperation
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class SequencesTest {
    @Test fun ifNotEmptyReturnsNullWhenEmpty() = assertNull(emptySequence<Int>().constrainOnce().ifNotEmpty())
    @Test fun ifNotEmptyReturnsEquivalentSequence() = sequenceOf(1, 2, 3).testIntermediateOperation({
        assertNotNull(ifNotEmpty())
    }) { assertValues(1, 2, 3) }
    @Test fun ifNotEmptyEnumeratesOriginalSequenceLazily() = sequence {
        yield(1)
        fail("Should not enumerate this far")
    }.testIntermediateOperation({ assertNotNull(ifNotEmpty()) }) { assertStartsWith(1) }
}
