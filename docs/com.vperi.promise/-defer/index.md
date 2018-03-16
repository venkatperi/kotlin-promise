[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Defer](./index.md)

# Defer

`interface Defer<V>`

### Parameters

`V` - Type of the promise's value.

### Properties

| Name | Summary |
|---|---|
| [promise](promise.md) | `abstract val promise: `[`Promise`](../-promise/index.md)`<`[`V`](index.md#V)`>`<br>The underlying promise |

### Functions

| Name | Summary |
|---|---|
| [reject](reject.md) | `abstract fun reject(error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Rejects the underlying promise with the given [error](reject.md#com.vperi.promise.Defer$reject(kotlin.Throwable)/error) |
| [resolve](resolve.md) | `abstract fun resolve(result: `[`V`](index.md#V)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Resolve's the promise with the given [result](resolve.md#com.vperi.promise.Defer$resolve(com.vperi.promise.Defer.V)/result), if still pending. |
