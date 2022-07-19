package com.tomuvak.util.sequences

/**
 * Returns the single element in the receiver sequence [this], or throws if the sequence is empty. Not supposed to be
 * called on a sequence with more than one element – but this function makes no effort to verify that that's not the
 * case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [first], but signals to the reader of calling code that the point is not to take the _first_
 * element (of potentially multiple elements), but rather the _only_ element (where the fact that there can't be
 * multiple elements has somehow been established prior to the call).
 */
fun <T> Sequence<T>.singleNoVerify(): T = first()

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or throws if there
 * is no element in the sequence which satisfies the predicate. Not supposed to be called on a sequence with more than
 * one element satisfying the predicate – but this function makes no effort to verify that that's not the case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [first], but signals to the reader of calling code that the point is not to take the _first_
 * element (of potentially multiple elements) which satisfies the predicate, but rather the _only_ element which does
 * (where the fact that there can't be multiple elements satisfying the predicate has somehow been established prior to
 * the call).
 */
fun <T> Sequence<T>.singleNoVerify(predicate: (T) -> Boolean): T = first(predicate)

/**
 * Returns the single element in the receiver sequence [this], or `null` if the sequence is empty. Not supposed to be
 * called on a sequence with more than one element – but this function makes no effort to verify that that's not the
 * case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [firstOrNull], but signals to the reader of calling code that the point is not to take the
 * _first_ element (of potentially multiple elements), but rather the _only_ element (where the fact that there can't be
 * multiple elements has somehow been established prior to the call).
 */
fun <T> Sequence<T>.singleNoVerifyOrNull(): T? = firstOrNull()

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate], or `null` if there
 * is no element in the sequence which satisfies the predicate. Not supposed to be called on a sequence with more than
 * one element satisfying the predicate – but this function makes no effort to verify that that's not the case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [firstOrNull], but signals to the reader of calling code that the point is not to take the
 * _first_ element (of potentially multiple elements) which satisfies the predicate, but rather the _only_ element which
 * does (where the fact that there can't be multiple elements satisfying the predicate has somehow been established
 * prior to the call).
 */
fun <T> Sequence<T>.singleNoVerifyOrNull(predicate: (T) -> Boolean): T? = firstOrNull(predicate)
