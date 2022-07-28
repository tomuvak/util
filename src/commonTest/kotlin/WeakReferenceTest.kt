package com.tomuvak.util

import com.tomuvak.testing.coroutines.asyncTest
import kotlin.test.*

class WeakReferenceTest {
    @Test fun weakReference() = asyncTest { generateAndVerifyWeakReference().assertTargetReclaimable() }

    private suspend fun generateAndVerifyWeakReference(): WeakReference<Any> {
        val data = Any()
        val reference = WeakReference(data)
        reference.assertTargetNotReclaimable()
        assertSame(data, assertNotNull(reference.targetOrNull))
        return reference
    }
}
