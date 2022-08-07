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

    @Test fun inNonNullableMapGetOrGets() = testGetOr(Key, Value) { nonNullableMap }
    @Test fun inNonNullableMapGetOrOnlyRequiresOneCallToGet() = testGetOr(Key, Value) { oneCallNonNullableMap }
    @Test fun inNonNullableMapGetOrReturnsDefault() = testGetOr(NonExistingKey, Default) { nonNullableMap }
    @Test fun inNonNullableMapGetOrOnlyRequiresOneCallToReturnDefault() =
        testGetOr(Key, Default) { oneCallEmptyNonNullableMap }

    @Test fun inNullableMapGetOrGets() = testGetOr(Key, Value) { nullableMap }
    @Test fun inNullableMapGetOrOnlyRequiresOneCallToGetNonNull() = testGetOr(Key, Value) { oneCallNullableMap }
    @Test fun inNullableMapGetOrGetsNull() = testGetOr(KeyWithNullValue, null) { nullableMap }
    @Test fun inNullableMapGetOrReturnsDefault() = testGetOr(NonExistingKey, Default) { nullableMap }

    private inline fun <reified V> testGetOr(key: String, expectedValue: V, mapProvider: () -> Map<String, V>) =
        test(mapProvider, key, expectedValue, Map<String, V>::getOr, Map<String, V>::getOr, Map<String, V>::getOr)

    private inline fun <M : Map<String, V>, reified V> test(
        mapProvider: () -> M,
        key: String,
        expectedValue: V,
        valueOverloadToTest: M.(String, V) -> V,
        providerOverloadToTest: M.(String, () -> V) -> V,
        functionOverloadToTest: M.(String, (String) -> V) -> V
    ) {
        val default = Default as V
        val isDefault = expectedValue == default
        val provider = if (isDefault) scriptedProvider(default) else mootProvider
        val function = if (isDefault) scriptedFunction(key to default) else mootFunction

        assertEquals(expectedValue, mapProvider().valueOverloadToTest(key, default))
        assertEquals(expectedValue, mapProvider().providerOverloadToTest(key, provider))
        assertEquals(expectedValue, mapProvider().functionOverloadToTest(key, function))
    }

    private fun <V> oneCallMap(valueOrNull: V?): Map<String, V> = object : Map<String, V> {
        private val getFunction: (String) -> V? = scriptedFunction(Key to valueOrNull)
        override fun get(key: String): V? = getFunction(key)

        override val entries: Set<Map.Entry<String, V>> get() = mootProvider()
        override val keys: Set<String> get() = mootProvider()
        override val size: Int get() = mootProvider()
        override val values: Collection<V> get() = mootProvider()
        override fun isEmpty(): Boolean = mootProvider()
        override fun containsValue(value: V): Boolean = mootFunction(value)
        override fun containsKey(key: String): Boolean = mootFunction(key)
    }
}
