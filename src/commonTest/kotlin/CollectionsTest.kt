package com.tomuvak.util

import com.tomuvak.testing.assertFailsWithTypeAndMessageContaining
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.mootProvider
import kotlin.collections.dropLast
import kotlin.collections.takeLast
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionsTest {
    private val mootCollection: Collection<Int> = object : Collection<Int> {
        override val size: Int get() = mootProvider()
        override fun isEmpty(): Boolean = mootProvider()
        override fun iterator(): Iterator<Int> = mootProvider()
        override fun containsAll(elements: Collection<Int>): Boolean = mootFunction(elements)
        override fun contains(element: Int): Boolean = mootFunction(element)
    }

    @Test fun dropLastFailsForNegativeArgument() {
        for (n in listOf(-3, -2, -1))
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("negative", n) {
                mootCollection.dropLast(n)
            }
    }

    @Test fun dropLastDropsLast() {
        for (elements in listOf(emptyList(), listOf(1), listOf(1, 2), listOf(3, 0, 5, 7, 4)))
            for (n in 0..(elements.size + 2))
                assertEquals(elements.dropLast(n), (elements as Collection<Int>).dropLast(n))
    }

    @Test fun takeLastFailsForNegativeArgument() {
        for (n in listOf(-3, -2, -1))
            assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("negative", n) {
                mootCollection.takeLast(n)
            }
    }

    @Test fun takeLastTakesLast() {
        for (elements in listOf(emptyList(), listOf(1), listOf(1, 2), listOf(3, 0, 5, 7, 4)))
            for (n in 0..(elements.size + 2))
                assertEquals(elements.takeLast(n), (elements as Collection<Int>).takeLast(n))
    }
}
