package com.tomuvak.util

fun <T> Pair<T, T>.asSequence(): Sequence<T> = sequenceOf(first, second)

fun <T, R> Pair<T, T>.map(transform: (T) -> R): Pair<R, R> = Pair(transform(first), transform(second))
fun <T, R> Pair<T, T>.flatMap(transform: (T) -> Pair<R, R>): Pair<R, R> = map(transform).flatten()
fun <T> Pair<Pair<T, T>, Pair<T, T>>.flatten(): Pair<T, T> = Pair(first.first, second.second)

fun <T> Triple<T, T, T>.asSequence(): Sequence<T> = sequenceOf(first, second, third)

fun <T, R> Triple<T, T, T>.map(transform: (T) -> R): Triple<R, R, R> =
    Triple(transform(first), transform(second), transform(third))
fun <T, R> Triple<T, T, T>.flatMap(transform: (T) -> Triple<R, R, R>): Triple<R, R, R> = map(transform).flatten()
fun <T> Triple<Triple<T, T, T>, Triple<T, T, T>, Triple<T, T, T>>.flatten(): Triple<T, T, T> =
    Triple(first.first, second.second, third.third)
