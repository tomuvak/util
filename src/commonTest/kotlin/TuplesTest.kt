package com.tomuvak.util

import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.scriptedFunction
import kotlin.test.Test
import kotlin.test.assertEquals

class TuplesTest {
    @Test fun pairAsSequence() = Pair(1, 2).asSequence().assertValues(1, 2)

    @Test fun mapsPair() = assertEquals(Pair("one", "two"), Pair(1, 2).map(scriptedFunction(1 to "one", 2 to "two")))
    @Test fun flatMapsPair() = assertEquals(
        Pair("one", "four"),
        Pair(1, 2).flatMap(scriptedFunction(1 to Pair("one", "two"), 2 to Pair("three", "four")))
    )
    @Test fun flattensPair() = assertEquals(Pair(1, 4), Pair(Pair(1, 2), Pair(3, 4)).flatten())

    @Test fun tripleAsSequence() = Triple(1, 2, 3).asSequence().assertValues(1, 2, 3)

    @Test fun mapsTriple() = assertEquals(
        Triple("one", "two", "three"),
        Triple(1, 2, 3).map(scriptedFunction(1 to "one", 2 to "two", 3 to "three"))
    )
    @Test fun flatMapsTriple() = assertEquals(
        Triple("one", "five", "nine"),
        Triple(1, 2, 3).flatMap(scriptedFunction(
            1 to Triple("one", "two", "three"),
            2 to Triple("four", "five", "six"),
            3 to Triple("seven", "eight", "nine")
        ))
    )
    @Test fun flattensTriple() = assertEquals(
        Triple(1, 5, 9),
        Triple(Triple(1, 2, 3), Triple(4, 5, 6), Triple(7, 8, 9)).flatten()
    )
}
