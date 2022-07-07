# `com.tomuvak.util` – a multi-platform Kotlin library with complementary utilities to the Kotlin standard library
This library is licensed under the [MIT License](https://en.wikipedia.org/wiki/MIT_License);
see [LICENSE.txt](LICENSE.txt).

## Scope
This library is intended for utilities each of which meets _both_ of the following criteria (according to the author's
subjective view):
1. It should (and hopefully one day will) be in the Kotlin standard library.
2. It _almost_ is in the Kotlin standard library.

Criterion no. 2 means that this library is intended for things which are similar and closely related to what is already
offered in the Kotlin standard library.
For example, the standard library offers
[`filter`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/filter.html),
[`filterIsInstance`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/filter-is-instance.html), and
[`takeWhile`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/take-while.html) – but no
[`takeWhileIsInstance`](https://github.com/tomuvak/util/commit/1117432c596d6d7815515bef52efbf279bf71bc6). Such a utility
naturally fits in with what is already there.
Another example: the standard library offers a standard way to constrain a Sequence to be iterable only once
([`constrainOnce`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/constrain-once.html)); the
[reverse](https://github.com/tomuvak/util/commit/6b11eb289b95a6c6a28e0500da20803ca31597dd) – the ability to take a
Sequence which is potentially only iterable once and wrap it in a structure which allows iterating over it multiple
times – complements it nicely.
Again, all according to the author's subjective view.

Criterion no. 1 alone is not enough for inclusion in this library. There's lots of functionality that could reasonably
be deemed to belong in the standard library (and which does belong in other languages' standard libraries), such as
working with [JSON](https://en.wikipedia.org/wiki/JSON), or an
[Optional type](https://en.wikipedia.org/wiki/Optional_type).
Such things, which (according to the author's subjective view) introduce new concepts (which might be considered basic,
but which are foreign to the Kotlin standard library), do not belong in `com.tomuvak.util`, but in other libraries (e.g.
[`com.tomuvak.optional`](https://github.com/tomuvak/optional)).
