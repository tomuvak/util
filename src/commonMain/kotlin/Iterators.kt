package com.tomuvak.util

/**
 * Returns `null` if the receiver iterator [this] is exhausted, or its next element if it's not.
 */
fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null
