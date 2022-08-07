package com.tomuvak.util

/**
 * Returns the value associated with the given [key] in the receiver map [this], or the given [default] if the key is
 * not in the map.
 *
 * Similar to the standard library's [getOrElse], but returns the value associated with the given [key] – rather than
 * the given [default] – also when that value is `null`. Also, this overload takes the default value itself, rather than
 * a function which computes it; there's another overload which takes such a function, as well as an overload which
 * takes a function to provide the default which receives the key as an argument.
 *
 * If the type [V] is not nullable, this operation is atomic (meaning it relies on a single call to the receiver map
 * [this]'s [get] function). If the type [V] is nullable, this operation is still atomic in case the map contains the
 * given [key] with a non-`null` value, but in case the map contains the key with a value of `null` or does not contain
 * the given key at all it uses a separate call to the map's [containsKey] function. This might have undesired effects
 * if the map is used concurrently by multiple threads; in particular, when the key is not in the map and is then added
 * to it by another thread, the call might return `null` even if the map never associated the given key with `null`.
 */
inline fun <K, reified V> Map<K, V>.getOr(key: K, default: V): V = getOr(key) { -> default }

/**
 * Returns the value associated with the given [key] in the receiver map [this], or, if the key is not in the map,
 * invokes the given [defaultProvider] and returns the result.
 *
 * Similar to the standard library's [getOrElse], but returns the value associated with the given [key] – rather than
 * calling the given [defaultProvider] and returning the result – also when that value is `null`.
 *
 * For passing the default value directly rather than a function which computes it, or to pass a function to compute the
 * default value which receives the key as an argument, use one of the other overloads.
 *
 * If the type [V] is not nullable, this operation is atomic (meaning it relies on a single call to the receiver map
 * [this]'s [get] function). If the type [V] is nullable, this operation is still atomic in case the map contains the
 * given [key] with a non-`null` value, but in case the map contains the key with a value of `null` or does not contain
 * the given key at all it uses a separate call to the map's [containsKey] function. This might have undesired effects
 * if the map is used concurrently by multiple threads; in particular, when the key is not in the map and is then added
 * to it by another thread, the call might return `null` even if the map never associated the given key with `null`.
 */
inline fun <K, reified V> Map<K, V>.getOr(key: K, defaultProvider: () -> V): V = getOr(key) { _ -> defaultProvider() }

/**
 * Returns the value associated with the given [key] in the receiver map [this], or, if the key is not in the map, calls
 * the given [defaultProvider] with the key as the argument and returns the result.
 *
 * Similar to the standard library's [getOrElse], but returns the value associated with the given [key] – rather than
 * invoking the given [defaultProvider] and returning the result – also when that value is `null`. Also, this overload
 * takes a function to compute the default value which receives the key as an argument; there's another overload which
 * takes a function with no parameters to compute the default, as well as an overload which takes the default value
 * itself rather than a function.
 *
 * If the type [V] is not nullable, this operation is atomic (meaning it relies on a single call to the receiver map
 * [this]'s [get] function). If the type [V] is nullable, this operation is still atomic in case the map contains the
 * given [key] with a non-`null` value, but in case the map contains the key with a value of `null` or does not contain
 * the given key at all it uses a separate call to the map's [containsKey] function. This might have undesired effects
 * if the map is used concurrently by multiple threads; in particular, when the key is not in the map and is then added
 * to it by another thread, the call might return `null` even if the map never associated the given key with `null`.
 */
inline fun <K, reified V> Map<K, V>.getOr(key: K, defaultProvider: (K) -> V): V {
    val ret = get(key)
    return ret ?: if (ret is V && containsKey(key)) ret else defaultProvider(key)
}
