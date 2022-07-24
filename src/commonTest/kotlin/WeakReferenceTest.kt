package com.tomuvak.util

import com.tomuvak.testing.coroutines.asyncTest
import com.tomuvak.testing.gc.tryToAchieveByForcingGc
import kotlinx.coroutines.coroutineScope
import kotlin.test.*

class WeakReferenceTest {
    @Test fun weakReference() = asyncTest {
        val reference = generateAndVerifyWeakReference()
        assertTrue(tryToAchieveByForcingGc { reference.targetOrNull == null })
    }

    private suspend fun generateAndVerifyWeakReference(): WeakReference<Any> {
        val data = Any()
        val reference = WeakReference(data)
        assertFalse(coroutineScope { tryToAchieveByForcingGc { reference.targetOrNull == null } })
        assertSame(data, assertNotNull(reference.targetOrNull))
        return reference
    }
}
