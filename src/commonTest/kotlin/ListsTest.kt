package com.tomuvak.util

import com.tomuvak.testing.assertions.assertFailsWithTypeAndMessageContaining
import kotlin.test.Test
import kotlin.test.assertEquals

class ListsTest {
    @Test fun replaceAt() {
        for (list in listOf(listOf(1), listOf(2, 2), listOf(1, 3, 3, 7)))
            for (index in list.indices)
                for (newValue in listOf(0, 1, 5))
                    assertEquals(
                        list.subList(0, index) + newValue + list.subList(index + 1, list.size),
                        list.replaceAt(index, newValue)
                    )
    }
    @Test fun replaceAtThrowsWhenIndexOutOfBounds() {
        for (list in listOf(emptyList(), listOf(1), listOf(2, 2), listOf(1, 3, 3, 7)))
            for (index in -3..list.size + 2)
                if (index !in list.indices)
                    for (newValue in listOf(0, 1, 5))
                        thenFails (index, list.size) { list.replaceAt(index, newValue) }
    }

    @Test fun replaceAtZero() {
        for (list in listOf(listOf(1), listOf(2, 2), listOf(1, 3, 3, 7)))
            assertEquals(list, list.replaceAt())
    }
    @Test fun replaceAtOne() {
        for (list in listOf(listOf(1), listOf(2, 2), listOf(1, 3, 3, 7)))
            for (index in list.indices)
                for (newValue in listOf(0, 1, 5))
                    assertEquals(list.replaceAt(index, newValue), list.replaceAt(index to newValue))
    }
    @Test fun replaceAtMultiple() {
        assertEquals(listOf(3, 4), listOf(1, 2).replaceAt(0 to 3, 1 to 4))
        assertEquals(listOf(7, 0, 8), listOf(0, 0, 1).replaceAt(2 to 8, 0 to 7))
        assertEquals(listOf(5, 0, 1, 8, 2), listOf(5, 6, 7, 8, 9).replaceAt(1 to 0, 4 to 2, 2 to 1))
        assertEquals(listOf(null, 1, 2), listOf(0, 1, null).replaceAt(0 to null, 2 to 2))
    }
    @Test fun replaceAtMultipleThrowsWhenIndexOutOfBounds() {
        thenFails(0, 0) { emptyList<Int>().replaceAt(0 to 3) }
        thenFails(-1, 1) { listOf(1).replaceAt(-1 to 4)}
        thenFails(4, 3) { listOf(0, 1, 2).replaceAt(0 to 5, 2 to 6, 4 to 7)}
    }
    @Test fun replaceAtMultipleThrowsWhenRepeatingIndex() {
        thenFails(0) { listOf(0).replaceAt(0 to 1, 0 to 1) }
        thenFails(1) { listOf(1, 2, 3).replaceAt(1 to 4, 2 to 4, 1 to 4)}
    }

    private fun thenFails(index: Int, size: Int, block: () -> Unit) =
        assertFailsWithTypeAndMessageContaining<IndexOutOfBoundsException>(index, size, block=block)
    private fun thenFails(index: Int, block: () -> Unit) =
        assertFailsWithTypeAndMessageContaining<IllegalArgumentException>("epeating index", index, block=block)
}
