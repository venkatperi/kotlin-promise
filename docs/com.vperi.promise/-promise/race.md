[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [race](./race.md)

# race

`@JvmStatic fun <V> race(promises: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Promise`](index.md)`<`[`V`](race.md#V)`>>): `[`Promise`](index.md)`<`[`V`](race.md#V)`>`

Returns a promise that resolves or rejects as soon as
one of the promises in the iterable resolves or rejects,
with the value or reason from that promise.

### Parameters

`promises` - [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) list of promises.

**Return**

A pending Promise that resolves or rejects
asynchronically as soon as one of the promises in the given
iterable resolves or rejects, adopting that first promise's
value as its value.



If the iterable passed is empty, the promise returned will be forever
pending.

