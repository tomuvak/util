package com.tomuvak.util

import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedFunction
import com.tomuvak.testing.assertions.scriptedProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class MapsTest {
    companion object {
        private const val Key: String = "Key"
        private const val Value: String = "Value"
        private const val KeyWithNullValue: String = "KeyWithNullValue"
        private const val NonExistingKey: String = "NonExistingKey"
        private const val Default: String = "Default"
    }

    private val nonNullableMap: Map<String, String> get() = mapOf(Key to Value)
    private val oneCallNonNullableMap: Map<String, String> get() = oneCallMap(Value)
    private val oneCallEmptyNonNullableMap: Map<String, String> get() = oneCallMap(null)

    private val nullableMap: Map<String, String?> get() = mapOf(Key to Value, KeyWithNullValue to null)
    private val oneCallNullableMap: Map<String, String?> get() = oneCallMap(Value)

    private val nonNullableMutableMap: MutableMap<String, String> get() = mutableMapOf(Key to Value)
    private val nullableMutableMap: MutableMap<String, String?> get() =
        mutableMapOf(Key to Value, KeyWithNullValue to null)

    @Test fun inNonNullableMapGetOrGets() = testGetOr(Key, Value) { nonNullableMap }
    @Test fun inNonNullableMapGetOrOnlyRequiresOneCallToGet() = testGetOr(Key, Value) { oneCallNonNullableMap }
    @Test fun inNonNullableMapGetOrReturnsDefault() = testGetOr(NonExistingKey, Default) { nonNullableMap }
    @Test fun inNonNullableMapGetOrOnlyRequiresOneCallToReturnDefault() =
        testGetOr(Key, Default) { oneCallEmptyNonNullableMap }

    @Test fun inNullableMapGetOrGets() = testGetOr(Key, Value) { nullableMap }
    @Test fun inNullableMapGetOrOnlyRequiresOneCallToGetNonNull() = testGetOr(Key, Value) { oneCallNullableMap }
    @Test fun inNullableMapGetOrGetsNull() = testGetOr(KeyWithNullValue, null) { nullableMap }
    @Test fun inNullableMapGetOrReturnsDefault() = testGetOr(NonExistingKey, Default) { nullableMap }

    @Test fun inNonNullableMutableMapPutIfAbsentAndGetGets() =
        nonNullableMutableMap.testPutIfAbsentAndGetWithSideEffects(Key, Value)
    @Test fun inNonNullableMutableMapPutIfAbsentAndGetOnlyRequiresOneCallToGet() =
        testPutIfAbsentAndGet(Key, Value) { oneCallMutableMap<String>(Value) }
    @Test fun inNonNullableMutableMapPutIfAbsentAndGetPutsAndReturnsDefault() =
        nonNullableMutableMap.testPutIfAbsentAndGetWithSideEffects(NonExistingKey, Default)
    @Test fun inNonNullableMutableMapPutIfAbsentAndGetOnlyRequiresOneGetToPutAndReturnDefault() =
        testPutIfAbsentAndGet(Key, Default) { oneGetMutableMap<String>(null) }

    @Test fun inNullableMutableMapPutIfAbsentAndGetGets() =
        nullableMutableMap.testPutIfAbsentAndGetWithSideEffects(Key, Value)
    @Test fun inNullableMutableMapPutIfAbsentAndGetOnlyRequiresOneCallToGetNonNull() =
        testPutIfAbsentAndGet(Key, Value) { oneCallMutableMap<String?>(Value) }
    @Test fun inNullableMutableMapPutIfAbsentAndGetGetsNull() =
        nullableMutableMap.testPutIfAbsentAndGetWithSideEffects(KeyWithNullValue, null)
    @Test fun inNullableMutableMapPutIfAbsentAndGetPutsAndReturnsDefault() =
        nullableMutableMap.testPutIfAbsentAndGetWithSideEffects(NonExistingKey, Default)

    private inline fun <reified V> testGetOr(key: String, expectedValue: V, mapProvider: () -> Map<String, V>) =
        test(
            mapProvider, key, expectedValue,
            // Use the following lines instead of the workaround lines below them once possible
            // (should be possible as of 1.8.0, see https://youtrack.jetbrains.com/issue/KT-53672):
            // Map<String, V>::getOr,
            // Map<String, V>::getOr,
            // Map<String, V>::getOr
            { key, default -> getOr(key, default) },
            { key, defaultProvider -> getOr(key, defaultProvider) },
            { key, defaultProvider -> getOr(key, defaultProvider) }
        )

    private inline fun <reified V> testPutIfAbsentAndGet(
        key: String, expectedValue: V, mapProvider: () -> MutableMap<String, V>
    ) = test(
        mapProvider, key, expectedValue,
        // Use the following lines instead of the workaround lines below them once possible
        // (should be possible as of 1.8.0, see https://youtrack.jetbrains.com/issue/KT-53672):
        // MutableMap<String, V>::putIfAbsentAndGet,
        // MutableMap<String, V>::putIfAbsentAndGet,
        // MutableMap<String, V>::putIfAbsentAndGet
        { key, default -> putIfAbsentAndGet(key, default) },
        { key, defaultProvider -> putIfAbsentAndGet(key, defaultProvider) },
        { key, defaultProvider -> putIfAbsentAndGet(key, defaultProvider) }
    )

    private inline fun <reified V> MutableMap<String, V>.testPutIfAbsentAndGetWithSideEffects(
        key: String, expectedValue: V
    ) {
        val expectedResult = toMutableMap()
        expectedResult[key] = expectedValue
        test(
            ::toMutableMap, key, expectedValue,
            // Use the following lines instead of the workaround lines below them once possible
            // (should be possible as of 1.8.0, see https://youtrack.jetbrains.com/issue/KT-53672):
            // MutableMap<String, V>::putIfAbsentAndGet,
            // MutableMap<String, V>::putIfAbsentAndGet,
            // MutableMap<String, V>::putIfAbsentAndGet
            { key, default -> putIfAbsentAndGet(key, default) },
            { key, defaultProvider -> putIfAbsentAndGet(key, defaultProvider) },
            { key, defaultProvider -> putIfAbsentAndGet(key, defaultProvider) }
        ) { assertEquals(expectedResult, it) }
    }

    private inline fun <M : Map<String, V>, reified V> test(
        mapProvider: () -> M,
        key: String,
        expectedValue: V,
        valueOverloadToTest: M.(String, V) -> V,
        providerOverloadToTest: M.(String, () -> V) -> V,
        functionOverloadToTest: M.(String, (String) -> V) -> V,
        extraVerification: (M) -> Unit = {}
    ) {
        val default = Default as V
        val isDefault = expectedValue == default
        val provider = if (isDefault) scriptedProvider(default) else mootProvider
        val function = if (isDefault) scriptedFunction(key to default) else mootFunction

        val maps = List(3) { mapProvider() }
        assertEquals(expectedValue, maps[0].valueOverloadToTest(key, default))
        assertEquals(expectedValue, maps[1].providerOverloadToTest(key, provider))
        assertEquals(expectedValue, maps[2].functionOverloadToTest(key, function))

        maps.forEach(extraVerification)
    }

    private fun <V> oneCallMap(valueOrNull: V?): Map<String, V> = oneCallMutableMap(valueOrNull)
    private fun <V> oneCallMutableMap(valueOrNull: V?): MutableMap<String, V> =
        oneGetMutableMap(valueOrNull) { key, value -> mootFunction(key to value) }
    private fun <V> oneGetMutableMap(
        valueOrNull: V?,
        put: (String, V) -> Unit = { _, _ -> }
    ): MutableMap<String, V> = object : MutableMap<String, V> {
        private val getFunction: (String) -> V? = scriptedFunction(Key to valueOrNull)
        override fun get(key: String): V? = getFunction(key)
        override fun put(key: String, value: V): V? {
            put(key, value)
            return null
        }

        override val entries: MutableSet<MutableMap.MutableEntry<String, V>> get() = mootProvider()
        override val keys: MutableSet<String> get() = mootProvider()
        override val size: Int get() = mootProvider()
        override val values: MutableCollection<V> get() = mootProvider()
        override fun clear() = mootProvider()
        override fun isEmpty(): Boolean = mootProvider()
        override fun remove(key: String): V? = mootFunction(key)
        override fun putAll(from: Map<out String, V>) = mootFunction(from)
        override fun containsValue(value: V): Boolean = mootFunction(value)
        override fun containsKey(key: String): Boolean = mootFunction(key)
    }
}
