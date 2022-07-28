package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.coroutines.asyncTest
import com.tomuvak.util.assertTargetOnlyReclaimableAfter
import com.tomuvak.util.dismissNext
import com.tomuvak.util.generateSequenceAndWeakReferences
import kotlin.test.*

class MultiTransformsTest {
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
        val (source, references) = generateSequenceAndWeakReferences(3)
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
