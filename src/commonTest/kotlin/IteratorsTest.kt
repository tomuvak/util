package com.tomuvak.util

import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedProvider
import kotlin.test.*

class IteratorsTest {
    private lateinit var mockHasNext: (() -> Boolean)
    private lateinit var mockNext: (() -> Int)

    private var iterator: Iterator<Int> = object : Iterator<Int> {
        override fun hasNext(): Boolean = mockHasNext()
        override fun next(): Int = mockNext()
    }

    @Test fun nextOrNullReturnsNull() {
        mockHasNext = scriptedProvider(false)
        mockNext = mootProvider
        assertNull(iterator.nextOrNull())
    }

    @Test fun nextOrNullReturnsNext() {
        mockHasNext = scriptedProvider(true)
        mockNext = scriptedProvider(3)
        assertEquals(3, iterator.nextOrNull())
    }

    @Test fun nextOrNullFailsWhenHasNextFails() {
        val failure = Exception("Failure in hasNext()")
        mockHasNext = { throw failure }
        mockNext = mootProvider
        assertSame(failure, assertFails { iterator.nextOrNull() })
    }

    @Test fun nextOrNullFailsWhenNextFails() {
        val failure = Exception("Failure in next()")
        mockHasNext = scriptedProvider(true)
        mockNext = { throw failure }
        assertSame(failure, assertFails { iterator.nextOrNull() })
    }
}
