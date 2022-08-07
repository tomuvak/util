package com.tomuvak.util

import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedFunction
import com.tomuvak.testing.assertions.scriptedProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Test fun inNonNullableMapGetOrValueGets() = assertEquals(Value, nonNullableMap.getOr(Key, Default))
    @Test fun inNonNullableMapGetOrProviderGets() = assertEquals(Value, nonNullableMap.getOr(Key, mootProvider))
    @Test fun inNonNullableMapGetOrFunctionGets() = assertEquals(Value, nonNullableMap.getOr(Key, mootFunction))

    @Test fun inNonNullableMapGetOrValueOnlyRequiresOneCallToGet() =
        assertEquals(Value, oneCallNonNullableMap.getOr(Key, Default))
    @Test fun inNonNullableMapGetOrProviderOnlyRequiresOneCallToGet() =
        assertEquals(Value, oneCallNonNullableMap.getOr(Key, mootProvider))
    @Test fun inNonNullableMapGetOrFunctionOnlyRequiresOneCallToGet() =
        assertEquals(Value, oneCallNonNullableMap.getOr(Key, mootFunction))

    @Test fun inNonNullableMapGetOrValueReturnsDefault() =
        assertEquals(Default, nonNullableMap.getOr(NonExistingKey, Default))
    @Test fun inNonNullableMapGetOrProviderReturnsDefault() =
        assertEquals(Default, nonNullableMap.getOr(NonExistingKey, scriptedProvider(Default)))
    @Test fun inNonNullableMapGetOrFunctionReturnsDefault() =
        assertEquals(Default, nonNullableMap.getOr(NonExistingKey, scriptedFunction(NonExistingKey to Default)))

    @Test fun inNonNullableMapGetOrValueOnlyRequiresOneCallToReturnDefault() =
        assertEquals(Default, oneCallEmptyNonNullableMap.getOr(Key, Default))
    @Test fun inNonNullableMapGetOrProviderOnlyRequiresOneCallToReturnDefault() =
        assertEquals(Default, oneCallEmptyNonNullableMap.getOr(Key, scriptedProvider(Default)))
    @Test fun inNonNullableMapGetOrFunctionOnlyRequiresOneCallToReturnDefault() =
        assertEquals(Default, oneCallEmptyNonNullableMap.getOr(Key, scriptedFunction(Key to Default)))

    @Test fun inNullableMapGetOrValueGets() = assertEquals(Value, nullableMap.getOr(Key, Default))
    @Test fun inNullableMapGetOrProviderGets() = assertEquals(Value, nullableMap.getOr(Key, mootProvider))
    @Test fun inNullableMapGetOrFunctionGets() = assertEquals(Value, nullableMap.getOr(Key, mootFunction))

    @Test fun inNullableMapGetOrValueOnlyRequiresOneCallToGetNonNull() =
        assertEquals(Value, oneCallNullableMap.getOr(Key, Default))
    @Test fun inNullableMapGetOrProviderOnlyRequiresOneCallToGetNonNull() =
        assertEquals(Value, oneCallNullableMap.getOr(Key, mootProvider))
    @Test fun inNullableMapGetOrFunctionOnlyRequiresOneCallToGetNonNull() =
        assertEquals(Value, oneCallNullableMap.getOr(Key, mootFunction))

    @Test fun inNullableMapGetOrValueGetsNull() = assertNull(nullableMap.getOr(KeyWithNullValue, Default))
    @Test fun inNullableMapGetOrProviderGetsNull() = assertNull(nullableMap.getOr(KeyWithNullValue, mootProvider))
    @Test fun inNullableMapGetOrFunctionGetsNull() = assertNull(nullableMap.getOr(KeyWithNullValue, mootFunction))

    @Test fun inNullableMapGetOrValueReturnsDefault() =
        assertEquals(Default, nullableMap.getOr(NonExistingKey, Default))
    @Test fun inNullableMapGetOrProviderReturnsDefault() =
        assertEquals(Default, nullableMap.getOr(NonExistingKey, scriptedProvider(Default)))
    @Test fun inNullableMapGetOrFunctionReturnsDefault() =
        assertEquals(Default, nullableMap.getOr(NonExistingKey, scriptedFunction(NonExistingKey to Default)))

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
