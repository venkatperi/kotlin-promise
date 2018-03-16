[kotlin-promise](../index.md) / [com.vperi.promise](./index.md)

## Package com.vperi.promise

### Types

| Name | Summary |
|---|---|
| [Configuration](-configuration/index.md) | `data class Configuration` |
| [Defer](-defer/index.md) | `interface Defer<V>` |
| [Promise](-promise/index.md) | `abstract class Promise<V>`<br>Represents the eventual completion (or failure) of an asynchronous operation, and its resulting value (or error). |
| [Result](-result/index.md) | `sealed class Result<out V>` |

### Type Aliases

| Name | Summary |
|---|---|
| [Executor](-executor.md) | `typealias Executor<V> = `[`Promise`](-promise/index.md)`<`[`V`](-executor.md#V)`>.((`[`V`](-executor.md#V)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [FailureHandler](-failure-handler.md) | `typealias FailureHandler<X> = (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`X`](-failure-handler.md#X) |
| [SuccessHandler](-success-handler.md) | `typealias SuccessHandler<V, X> = (`[`V`](-success-handler.md#V)`) -> `[`X`](-success-handler.md#X) |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [java.util.concurrent.Future](java.util.concurrent.-future/index.md) |  |

### Functions

| Name | Summary |
|---|---|
| [deferred](deferred.md) | `fun <V> deferred(): `[`Defer`](-defer/index.md)`<`[`V`](deferred.md#V)`>`<br>Returns a [Defer](-defer/index.md) object which can be eventually settled |
| [delay](delay.md) | `fun <V> `[`Promise`](-promise/index.md)`<`[`V`](delay.md#V)`>.delay(msTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Promise`](-promise/index.md)`<`[`V`](delay.md#V)`>` |
| [promise](promise.md) | `fun <V> promise(executor: `[`Executor`](-executor.md)`<`[`V`](promise.md#V)`>): `[`Promise`](-promise/index.md)`<`[`V`](promise.md#V)`>`<br>Returns a promise`fun <V> promise(value: `[`V`](promise.md#V)`): `[`Promise`](-promise/index.md)`<`[`V`](promise.md#V)`>`<br>Returns a already settled promise that resolves to [value](promise.md#com.vperi.promise$promise(com.vperi.promise.promise.V)/value)`fun <V> promise(error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](-promise/index.md)`<`[`V`](promise.md#V)`>`<br>Returns a already settled promise that rejects with [error](promise.md#com.vperi.promise$promise(kotlin.Throwable)/error) |
| [then](then.md) | `fun <V, X> `[`Promise`](-promise/index.md)`<`[`Promise`](-promise/index.md)`<`[`V`](then.md#V)`>>.then(onResolved: `[`SuccessHandler`](-success-handler.md)`<`[`V`](then.md#V)`, `[`X`](then.md#X)`>): `[`Promise`](-promise/index.md)`<`[`X`](then.md#X)`>` |
