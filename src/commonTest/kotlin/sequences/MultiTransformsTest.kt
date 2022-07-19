package com.tomuvak.util.sequences

import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.coroutines.asyncTest
import com.tomuvak.testing.gc.tryToAchieveByForcingGc
import com.tomuvak.util.WeakReference
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
        fun generateElementWithReference(): Pair<Any, WeakReference<Any>> = Any().let { it to WeakReference(it) }
        fun <T> Iterator<T>.dismissNext() { next() } // Needs to be in a separate function to avoid hidden references

        val references = mutableListOf<WeakReference<Any>>()
        val source = sequence { repeat(3) { yield(generateElementWithReference())} }.map { (element, reference) ->
            assertSame(element, assertNotNull(reference.targetOrNull))
            references.add(reference)
            element
        }

        val iterators = source.transform(listOf({ it }, { it })).map { it.iterator() }

        iterators[0].dismissNext()
        assertFalse(tryToAchieveByForcingGc { references[0].targetOrNull == null })
        iterators[1].dismissNext()
        assertTrue(tryToAchieveByForcingGc { references[0].targetOrNull == null })

        iterators[1].dismissNext()
        assertFalse(tryToAchieveByForcingGc { references[1].targetOrNull == null })
        iterators[0].dismissNext()
        assertTrue(tryToAchieveByForcingGc { references[1].targetOrNull == null })

        iterators[0].dismissNext()
        assertFalse(tryToAchieveByForcingGc { references[2].targetOrNull == null })
        iterators[1].dismissNext()
        assertTrue(tryToAchieveByForcingGc { references[2].targetOrNull == null })
    }

    private fun thenHasTransformedCorrectly(transformed: List<Sequence<Int>>) {
        assertEquals(transforms.size, transformed.size)
        for ((transform, result) in transforms.zip(transformed))
            result.assertValues(*transform(source).toList().toTypedArray())
    }
}
