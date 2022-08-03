package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedFunction
import com.tomuvak.testing.coroutines.asyncTest
import com.tomuvak.testing.gc.Wrapper
import com.tomuvak.testing.gc.assertTargetOnlyReclaimableAfter
import com.tomuvak.testing.gc.dismissNext
import com.tomuvak.testing.gc.generateSequenceAndWeakReferences
import com.tomuvak.util.map
import kotlin.test.*

class TransformTest {
    private val source: Sequence<String> = sequenceOf("abc", "", "x", "tyuiop")
    private val transforms: List<(Sequence<String>) -> Sequence<Int>> = listOf(
        { it.map { it.length } },
        { it.map { it.length }.filter { it > 1 } },
        { it.map { it.length }.filter { it > 0 }.map { it * 2} }
    )

    @Test fun transformTransforms() = thenHasTransformedCorrectly(source.transform(transforms))

    @Test fun transformDoesNotIterateSourceMultipleTimes() =
        thenHasTransformedCorrectly(source.constrainOnce().transform(transforms))

    @Test fun transformDoesNotIterateSourceBeforeItHasTo() { Sequence<String>(mootProvider).transform(transforms) }

    @Test fun transformDoesNotIterateSourceFurtherThanItHasTo() {
        var numEnumerated = 0

        val iterators = source.onEach { numEnumerated++ }.transform(transforms).map { it.iterator() }
        assertEquals(0, numEnumerated)

        assertEquals(3, iterators[1].next())
        assertEquals(6, iterators[2].next())
        assertEquals(3, iterators[0].next())
        assertEquals(1, numEnumerated)

        assertEquals(0, iterators[0].next())
        assertEquals(2, numEnumerated)

        assertEquals(2, iterators[2].next())
        assertEquals(1, iterators[0].next())
        assertEquals(3, numEnumerated)
    }

    @Test fun transformDoesNotLetSingleTransformIterateSequenceTwice() {
        val transformed = source.transform(transforms)[0]
        transformed.iterator()
        assertFailsWith<IllegalStateException> { transformed.iterator() }
    }

    @Test fun transformResultCanBeIteratedMultipleTimesIfDoesNotRequireReiterationOfSource() {
        val transformed = source.transform(listOf({ it.cached() }))[0]
        transformed.iterator()
        transformed.iterator()
    }

    @Test fun transformForgetsNoLongerNeededSourceElements() = asyncTest {
        val (source, references) = generateSequenceAndWeakReferences(3) { Any() }
        val iterators = source.transform(listOf({ it }, { it })).map { it.iterator() }

        iterators[0].dismissNext()
        references[0].assertTargetOnlyReclaimableAfter { iterators[1].dismissNext() }

        iterators[1].dismissNext()
        references[1].assertTargetOnlyReclaimableAfter { iterators[0].dismissNext() }

        iterators[0].dismissNext()
        references[2].assertTargetOnlyReclaimableAfter { iterators[1].dismissNext() }
    }

    private fun thenHasTransformedCorrectly(transformed: List<Sequence<Int>>) {
        assertEquals(transforms.size, transformed.size)
        for ((transform, result) in transforms.zip(transformed))
            result.assertValues(*transform(source).toList().toTypedArray())
    }
}

class PartitionIntermediateTest {
    @Test fun partitionIntermediatePartitions() {
        for (source in listOf(emptySequence(), sequenceOf(0), sequenceOf(1), sequenceOf(1, 2, 4, 3, 7, 5, 6, 0))) {
            val partitions = source.partition { it % 2 == 0 }
            val partitionsIntermediate = source.partitionIntermediate { it % 2 == 0 }.map { it.toList() }
            assertEquals(partitions, partitionsIntermediate)
        }
    }

    @Test fun partitionIntermediateDoesNotIterateSourceMultipleTimes() {
        val source = sequenceOf(1, 2, 4, 3, 7, 5, 6, 0)
        val partitions = source.partition { it % 2 == 0 }
        val partitionsIntermediate = source.constrainOnce().partitionIntermediate { it % 2 == 0 }.map { it.toList() }
        assertEquals(partitions, partitionsIntermediate)
    }

    @Test fun partitionIntermediateDoesNotIterateSourceBeforeItHasTo() {
        Sequence<Int>(mootProvider).partitionIntermediate(mootFunction)
    }

    @Test fun partitionIntermediateDoesNotIterateSourceFurtherThanItHasTo() {
        var numEnumerated = 0
        val source = sequenceOf(1, 2, 4, 3, 7, 5, 6, 0).onEach { numEnumerated++ }
        val (evens, odds) = source.partitionIntermediate { it % 2 == 0 }.map { it.iterator() }
        assertEquals(0, numEnumerated)

        assertEquals(2, evens.next())
        assertEquals(1, odds.next())
        assertEquals(2, numEnumerated)

        assertEquals(3, odds.next())
        assertEquals(4, numEnumerated)

        assertEquals(7, odds.next())
        assertEquals(4, evens.next())
        assertEquals(5, numEnumerated)

        assertEquals(6, evens.next())
        assertEquals(7, numEnumerated)
    }

    @Test fun partitionIntermediateOnlyEvaluatesPredicateOncePerElement() = assertEquals(
        Pair(listOf(1), listOf(2)),
        sequenceOf(1, 2).partitionIntermediate(scriptedFunction(1 to true, 2 to false)).map { it.toList() }
    )

    @Test fun partitionIntermediateForgetsSourceElements() = asyncTest {
        val (source, references) = generateSequenceAndWeakReferences(3) { Wrapper(it) }
        val (evens, odds) = source.partitionIntermediate { it.value % 2 == 0 }.map { it.iterator() }

        evens.dismissNext()
        references[0].assertTargetOnlyReclaimableAfter { odds.dismissNext() }
        references[1].assertTargetOnlyReclaimableAfter { evens.dismissNext() }
        references[2].assertTargetOnlyReclaimableAfter { assertFalse(odds.hasNext()) }
    }
}

class UnzipIntermediateTest {
    @Test fun unzipIntermediateUnzips() {
        for (source in listOf(
            emptySequence(),
            sequenceOf(1 to "a"),
            sequenceOf(1 to "a", 2 to "b"),
            sequenceOf(1 to "a", 2 to "b", 3 to "c")
        )) {
            val unzipped = source.unzip()
            val unzippedIntermediate = source.unzipIntermediate().map { it.toList() }
            assertEquals(unzipped, unzippedIntermediate)
        }
    }

    @Test fun unzipIntermediateDoesNotIterateSourceMultipleTimes() {
        val source = sequenceOf(1 to "a", 2 to "b", 3 to "c")
        val unzipped = source.unzip()
        val unzippedIntermediate = source.constrainOnce().unzipIntermediate().map { it.toList() }
        assertEquals(unzipped, unzippedIntermediate)
    }

    @Test fun unzipIntermediateDoesNotIterateSourceBeforeItHasTo() {
        Sequence<Pair<Int, String>>(mootProvider).unzipIntermediate()
    }

    @Test fun unzipIntermediateDoesNotIterateSourceFurtherThanItHasTo() {
        var numEnumerated = 0
        val source = sequenceOf(1 to "a", 2 to "b", 3 to "c", 4 to "d").onEach { numEnumerated++ }
        val (firsts, seconds) = source.unzipIntermediate().map { it.iterator() }
        assertEquals(0, numEnumerated)

        assertEquals(1, firsts.next())
        assertEquals("a", seconds.next())
        assertEquals(1, numEnumerated)

        assertEquals("b", seconds.next())
        assertEquals(2, numEnumerated)

        assertEquals("c", seconds.next())
        assertEquals(2, firsts.next())
        assertEquals(3, firsts.next())
        assertEquals(3, numEnumerated)
    }

    @Test fun unzipIntermediateForgetsSourceElements() = asyncTest {
        val (source, references) = generateSequenceAndWeakReferences(3) { Pair(Any(), Any()) }
        val (firsts, seconds) = source.unzipIntermediate().map { it.iterator() }

        firsts.dismissNext()
        references[0].assertTargetOnlyReclaimableAfter { seconds.dismissNext() }
        seconds.dismissNext()
        references[1].assertTargetOnlyReclaimableAfter { firsts.dismissNext() }
        firsts.dismissNext()
        references[2].assertTargetOnlyReclaimableAfter { seconds.dismissNext() }
    }
}
