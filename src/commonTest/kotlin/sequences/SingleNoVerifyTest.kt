package com.tomuvak.util.sequences

import com.tomuvak.testing.MockFunction
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.testLazyTerminalOperation
import com.tomuvak.testing.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SingleNoVerifyTest {
    private val alwaysFalse: MockFunction<Int, Boolean> = MockFunction { false }

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
}
