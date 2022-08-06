package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.*
import com.tomuvak.testing.coroutines.asyncTest
import com.tomuvak.testing.gc.assertTargetOnlyReclaimableAfter
import com.tomuvak.testing.gc.assertTargetsOnlyReclaimableAfter
import com.tomuvak.testing.gc.dismissNext
import com.tomuvak.testing.gc.generateSequenceAndWeakReferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class DropLastTest {
    @Test fun dropLastFailsOnNegativeArgument() {
        for (n in listOf(-1, -2, -3))
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("negative", n) {
                Sequence<Int>(mootProvider).dropLast(n)
            }
    }

    @Test fun dropLastReturnsSameSequenceWithoutAttemptedIterationWhenDroppingZeroElements() {
        val sequence = Sequence<Int>(mootProvider)
        assertSame(sequence, sequence.dropLast(0))
    }

    @Test fun dropLastDoesNotIterateSequenceBeforeItHasTo() { Sequence<Int>(mootProvider).dropLast(1) }

    @Test fun dropLastDropsLast() {
        for (elements in listOf(emptyList(), listOf(3), listOf(5, 2, 7, 4)))
            for (n in 0..(elements.count() + 2))
                elements.asSequence().testIntermediateOperation({ dropLast(n) }) {
                    assertValues(*elements.dropLast(n).toTypedArray())
                }
    }

    @Test fun dropLastDoesNotIterateSequenceFurtherThanItHasTo() {
        var numEnumerated = 0
        val dropped = sequenceOf(9, 2, 0, 3, 7, 6, 5, 8).onEach { numEnumerated++ }.dropLast(3).iterator()
        assertEquals(0, numEnumerated)

        assertEquals(9, dropped.next())
        assertEquals(4, numEnumerated)

        assertEquals(2, dropped.next())
        assertEquals(5, numEnumerated)

        assertEquals(0, dropped.next())
        assertEquals(6, numEnumerated)

        assertEquals(3, dropped.next())
        assertEquals(7, numEnumerated)
    }

    @Test fun dropLastDoesNotHoldOnToPastElements() = asyncTest {
        val (sequence, references) = generateSequenceAndWeakReferences(7) { Any() }
        val dropped = sequence.dropLast(3).iterator()
        repeat(4) { references[it].assertTargetOnlyReclaimableAfter(dropped::dismissNext) }
    }

    @Test fun dropLastDoesNotHoldOnToDroppedElements() = asyncTest {
        val (sequence, references) = generateSequenceAndWeakReferences(7) { Any() }
        val dropped = sequence.dropLast(3).iterator()
        repeat(4) { dropped.dismissNext() }
        references.drop(4).assertTargetsOnlyReclaimableAfter { assertFalse(dropped.hasNext()) }
    }

    @Test fun dropLastDoesNotHoldOnToElementsWhenAllDroppedBeforeActualIteration() = asyncTest {
        val (sequence, references) = generateSequenceAndWeakReferences(3) { Any() }
        val dropped = sequence.dropLast(4).iterator()
        references.assertTargetsOnlyReclaimableAfter { assertFalse(dropped.hasNext()) }
    }
}

class DropLastWhileTest {
    @Test fun dropLastWhileDoesNotIterateSequenceBeforeItHasTo() {
        Sequence<Int>(mootProvider).dropLastWhile(mootFunction)
    }

    @Test fun dropLastWhileDropsLastWhile() {
        for (elements in listOf(emptyList(), listOf(1), listOf(1, 2), listOf(1, 2, 3, 4, 5)))
            for (predicate in listOf<(Int) -> Boolean>({ it % 2 == 0 }, { it % 2 != 0}, { it < 3}, { it > 2 }))
                elements.asSequence().testIntermediateOperation({ dropLastWhile(predicate) }) {
                    assertValues(*elements.dropLastWhile(predicate).toTypedArray())
                }
    }

    @Test fun dropLastWhileDoesNotIterateSequenceFurtherThanItHasTo() {
        var numEnumerated = 0
        val dropped = sequenceOf(0, 1, 2, 3, 4, 5, 6, 7).onEach {
            numEnumerated++
        }.dropLastWhile { it % 4 > 1 }.iterator()
        assertEquals(0, numEnumerated)

        assertEquals(0, dropped.next())
        assertEquals(1, numEnumerated)

        assertEquals(1, dropped.next())
        assertEquals(2, numEnumerated)

        assertEquals(2, dropped.next())
        assertEquals(3, dropped.next())
        assertEquals(4, dropped.next())
        assertEquals(5, numEnumerated)

        assertEquals(5, dropped.next())
        assertEquals(6, numEnumerated)
    }
}

class TakeLastTest {
    @Test fun takeLastFailsOnNegativeArgument() {
        for (n in listOf(-1, -2, -3))
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("negative", n) {
                Sequence<Int>(mootProvider).takeLast(n)
            }
    }

    @Test fun takeLastDoesNotAttemptToIterateSequenceWhenZeroElementsRequested() =
        assertEquals(emptyList(), Sequence<Int>(mootProvider).takeLast(0))

    @Test fun takeLastTakesLast() {
        for (elements in listOf(emptyList(), listOf(3), listOf(5, 2, 7, 4)))
            for (n in 0..(elements.size + 2))
                elements.asSequence().testTerminalOperation({ takeLast(n) }) { assertEquals(elements.takeLast(n), it) }
    }
}

class TakeLastWhileTest {
    @Test fun takeLastWhileTakesLastWhile() {
        for (elements in listOf(emptyList(), listOf(1), listOf(1, 2), listOf(1, 2, 3, 4, 5)))
            for (predicate in listOf<(Int) -> Boolean>({ it % 2 == 0 }, { it % 2 != 0}, { it < 3}, { it > 2 }))
                elements.asSequence().testTerminalOperation({ takeLastWhile(predicate) }) {
                    assertEquals(elements.takeLastWhile(predicate), it)
                }
    }

    @Test fun takeLastWhileIsInstanceTakesLastWhileIsInstance() {
        fun test(vararg elements: Number, continuation: List<Int>.() -> Unit) =
            elements.asSequence().testTerminalOperation({ takeLastWhileIsInstance<Int>() }) { it.continuation() }
        fun List<Int>.then(vararg elements: Int) = assertEquals(elements.toList(), this)

        test() { then() }
        test(1) { then(1) }
        test(1L) { then() }
        test(1, 2) { then(1, 2) }
        test(1, 2L) { then() }
        test(1L, 2) { then(2) }
        test(1L, 2L) { then() }
    }
}
