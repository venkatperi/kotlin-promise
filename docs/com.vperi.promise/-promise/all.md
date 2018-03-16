[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [all](./all.md)

# all

`@JvmStatic fun <V> all(promises: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Promise`](index.md)`<`[`V`](all.md#V)`>>): `[`Promise`](index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`V`](all.md#V)`>>`

Returns a single Promise that resolves when all of the promises
in the iterable argument have resolved or when the iterable argument
contains no promises. It rejects immediately with the reason of the
first promise that rejects without waiting for the other promises
to settle.

### Parameters

`promises` - [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) list of promises.

**Return**
Promise which resolves to a [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html) of all resolved values
from the supplied list of [promises](all.md#com.vperi.promise.Promise.Companion$all(kotlin.collections.List((com.vperi.promise.Promise((com.vperi.promise.Promise.Companion.all.V)))))/promises), if all promises resolve,
or assumes the reason of rejection of the first promise that rejects.

