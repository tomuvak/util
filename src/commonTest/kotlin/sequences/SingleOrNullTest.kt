package com.tomuvak.util.sequences

import com.tomuvak.testing.MockFunction
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.testLazyTerminalOperation
import com.tomuvak.testing.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SingleOrNullTest {
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
}
