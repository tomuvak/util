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
                        assertFailsWithTypeAndMessageContaining<IndexOutOfBoundsException>(index, list.size) {
                            list.replaceAt(index, newValue)
                        }
    }
}
