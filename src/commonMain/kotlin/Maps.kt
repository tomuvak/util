package com.tomuvak.util

/**
 * Returns the value associated with the given [key] in the receiver map [this], or the given [default] if the key is
 * not in the map.
 *
 * Similar to [putIfAbsentAndGet] (for a mutable map), but that function also associates the given key with the given
 * default value in the map in case the map does not already contain the given key.
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
 * Similar to [putIfAbsentAndGet] (for a mutable map), but that function also associates the given key with the computed
 * default value in the map in case the map does not already contain the given key.
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
 * Similar to [putIfAbsentAndGet] (for a mutable map), but that function also associates the given key with the computed
 * default value in the map in case the map does not already contain the given key.
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

/**
 * Returns the value associated with the given [key] in the receiver mutable map [this], or, if the key is not in the
 * map, associates the key with the given [default] and returns it.
 *
 * Similar to [getOr], but when the map does not contain the given key this function not only returns the given default,
 * but also associates the given key with it in the map.
 *
 * Similar to the standard library's [getOrPut], but returns the value associated with the given [key] – rather than
 * associating it with the given [default] and returning it – also when that value is `null`. Also, this overload takes
 * the default value itself, rather than a function which computes it; there's another overload which takes such a
 * function, as well as an overload which takes a function to provide the default which receives the key as an argument.
 *
 * This operation is atomic (meaning it relies on a single call to the receiver mutable map [this]'s [get] function)
 * when the map contains the given [key] with an associated value which is not `null`. In all other cases (that is when
 * the map does not contain the key, or, in case the type [V] is nullable, when it contains the key with an associated
 * value of `null`), the operation is subject to race conditions when the map is changed concurrently, and may
 * potentially exhibit behavior and/or results inconsistent with the map's state (such as returning a value the map
 * never associated with the given key, or associating the given key with the given default value when the map already
 * contains the key).
 */
inline fun <K, reified V> MutableMap<K, V>.putIfAbsentAndGet(key: K, default: V): V =
    putIfAbsentAndGet<K, V>(key) { -> default }

/**
 * Returns the value associated with the given [key] in the receiver mutable map [this], or, if the key is not in the
 * map, invokes the given [defaultProvider] and associates the key with the result and returns it.
 *
 * Similar to [getOr], but when the map does not contain the given key this function not only computes and returns a
 * default, but also associates the given key with it in the map.
 *
 * Similar to the standard library's [getOrPut], but returns the value associated with the given [key] – rather than
 * calling the given [defaultProvider] and associating the key with the result and returning it – also when that value
 * is `null`.
 *
 * For passing the default value directly rather than a function which computes it, or to pass a function to compute the
 * default value which receives the key as an argument, use one of the other overloads.
 *
 * This operation is atomic (meaning it relies on a single call to the receiver mutable map [this]'s [get] function)
 * when the map contains the given [key] with an associated value which is not `null`. In all other cases (that is when
 * the map does not contain the key, or, in case the type [V] is nullable, when it contains the key with an associated
 * value of `null`), the operation is subject to race conditions when the map is changed concurrently, and may
 * potentially exhibit behavior and/or results inconsistent with the map's state (such as returning a value the map
 * never associated with the given key, or invoking the given [defaultProvider] and associating the key with the result
 * when the map already contains the key).
 */
inline fun <K, reified V> MutableMap<K, V>.putIfAbsentAndGet(key: K, defaultProvider: () -> V): V =
    putIfAbsentAndGet<K, V>(key) { _ -> defaultProvider() }

/**
 * Returns the value associated with the given [key] in the receiver mutable map [this], or, if the key is not in the
 * map, calls the given [defaultProvider] with the key as the argument and associates the key with the result and
 * returns it.
 *
 * Similar to [getOr], but when the map does not contain the given key this function not only computes and returns a
 * default, but also associates the given key with it in the map.
 *
 * Similar to the standard library's [getOrPut], but returns the value associated with the given [key] – rather than
 * calling the given [defaultProvider] and associating the key with the result and returning it – also when that value
 * is `null`. Also, this overload takes a function to compute the default value which receives the key as an argument;
 * there's another overload which takes a function with no parameters to compute the default, as well as an overload
 * which takes the default value itself rather than a function.
 *
 * This operation is atomic (meaning it relies on a single call to the receiver mutable map [this]'s [get] function)
 * when the map contains the given [key] with an associated value which is not `null`. In all other cases (that is when
 * the map does not contain the key, or, in case the type [V] is nullable, when it contains the key with an associated
 * value of `null`), the operation is subject to race conditions when the map is changed concurrently, and may
 * potentially exhibit behavior and/or results inconsistent with the map's state (such as returning a value the map
 * never associated with the given key, or invoking the given [defaultProvider] and associating the key with the result
 * when the map already contains the key).
 */
inline fun <K, reified V> MutableMap<K, V>.putIfAbsentAndGet(key: K, defaultProvider: (K) -> V): V =
    getOr(key) { -> defaultProvider(key).also { put(key, it) } }
