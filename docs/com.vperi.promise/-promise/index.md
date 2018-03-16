[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](./index.md)

# Promise

`abstract class Promise<V>`

Represents the eventual completion (or failure) of an asynchronous
operation, and its resulting value (or error).

Creating Promises:

**See Also**

[promise](../promise.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Promise()`<br>Represents the eventual completion (or failure) of an asynchronous operation, and its resulting value (or error). |

### Properties

| Name | Summary |
|---|---|
| [isDone](is-done.md) | `abstract val isDone: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>True if the promise has settled. |

### Functions

| Name | Summary |
|---|---|
| [cancel](cancel.md) | `abstract fun cancel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Cancels a pending. cancel() has no effect if the promise has already settled. Otherwise the promise will be rejected with a [InterruptedException](http://docs.oracle.com/javase/6/docs/api/java/lang/InterruptedException.html). |
| [catch](catch.md) | `fun catch(onRejected: `[`FailureHandler`](../-failure-handler.md)`<`[`V`](index.md#V)`>): `[`Promise`](./index.md)`<`[`V`](index.md#V)`>`<br>Returns a pending promise which handles rejection cases only. If the original promise rejects, [onRejected](catch.md#com.vperi.promise.Promise$catch(kotlin.Function1((kotlin.Throwable, com.vperi.promise.Promise.V)))/onRejected) is called with the reason of rejection. |
| [catchX](catch-x.md) | `fun <X> catchX(onRejected: `[`FailureHandler`](../-failure-handler.md)`<`[`X`](catch-x.md#X)`?>): `[`Promise`](./index.md)`<`[`X`](catch-x.md#X)`?>`<br>Like [catch](catch.md), catchX() handles rejection cases for the current promise. Where it differs is that it allows the failure handler to return an arbitrary type, and does not propagate the current promise's value on success.  If the current promise succeeds, catchX() will attempt to cast the fulfillment value to the new type and return that, or null. |
| [finally](finally.md) | `fun <X> finally(handler: (`[`Result`](../-result/index.md)`<`[`V`](index.md#V)`>) -> `[`X`](finally.md#X)`): `[`Promise`](./index.md)`<`[`X`](finally.md#X)`>`<br>Returns a pending promise. The single argument [handler](finally.md#com.vperi.promise.Promise$finally(kotlin.Function1((com.vperi.promise.Result((com.vperi.promise.Promise.V)), com.vperi.promise.Promise.finally.X)))/handler) is called when the promise settles with either the fulfillment value or rejection reason. |
| [then](then.md) | `fun <X> then(onResolved: `[`SuccessHandler`](../-success-handler.md)`<`[`V`](index.md#V)`, `[`X`](then.md#X)`>, onRejected: `[`FailureHandler`](../-failure-handler.md)`<`[`X`](then.md#X)`>): `[`Promise`](./index.md)`<`[`X`](then.md#X)`>`<br>Returns a pending promise. When the current promise settles, the handler function, either [onResolved](then.md#com.vperi.promise.Promise$then(kotlin.Function1((com.vperi.promise.Promise.V, com.vperi.promise.Promise.then.X)), kotlin.Function1((kotlin.Throwable, com.vperi.promise.Promise.then.X)))/onResolved) or [onRejected](then.md#com.vperi.promise.Promise$then(kotlin.Function1((com.vperi.promise.Promise.V, com.vperi.promise.Promise.then.X)), kotlin.Function1((kotlin.Throwable, com.vperi.promise.Promise.then.X)))/onRejected), gets called asynchronously with the settlement value if the current promise resolves, or the reason if the current promise rejects.`fun <X> then(onResolved: `[`SuccessHandler`](../-success-handler.md)`<`[`V`](index.md#V)`, `[`X`](then.md#X)`>): `[`Promise`](./index.md)`<`[`X`](then.md#X)`>`<br>Returns a pending promise. Accepts a single handler for fulfillment. If the original promise rejects, the returned promise rejects with the original's reason of rejection.`fun then(): `[`Promise`](./index.md)`<`[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`>`<br>Returns a pending promise. When the current promise settles, the returned promise enters the same settlement state. However, the fulfillment value of the original promise is not propagated. |

### Companion Object Properties

| Name | Summary |
|---|---|
| [config](config.md) | `var config: `[`Configuration`](../-configuration/index.md) |
| [executorService](executor-service.md) | `val executorService: `[`ExecutorService`](http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html) |
| [onUnhandledException](on-unhandled-exception.md) | `val onUnhandledException: Event<`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`>` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [all](all.md) | `fun <V> all(promises: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Promise`](./index.md)`<`[`V`](all.md#V)`>>): `[`Promise`](./index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`V`](all.md#V)`>>`<br>Returns a single Promise that resolves when all of the promises in the iterable argument have resolved or when the iterable argument contains no promises. It rejects immediately with the reason of the first promise that rejects without waiting for the other promises to settle. |
| [allDone](all-done.md) | `fun <V> allDone(promises: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Promise`](./index.md)`<`[`V`](all-done.md#V)`>>): `[`Promise`](./index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Result`](../-result/index.md)`<`[`V`](all-done.md#V)`>>>` |
| [race](race.md) | `fun <V> race(promises: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Promise`](./index.md)`<`[`V`](race.md#V)`>>): `[`Promise`](./index.md)`<`[`V`](race.md#V)`>`<br>Returns a promise that resolves or rejects as soon as one of the promises in the iterable resolves or rejects, with the value or reason from that promise. |
| [reject](reject.md) | `fun reject(reason: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](./index.md)`<`[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`>`<br>`fun <V> reject(reason: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](./index.md)`<`[`V`](reject.md#V)`>`<br>Create a already settled promise in the rejected state |
| [resolve](resolve.md) | `fun <V> resolve(value: `[`V`](resolve.md#V)`): `[`Promise`](./index.md)`<`[`V`](resolve.md#V)`>`<br>Created a already settled promise in the resolved state. |

### Extension Functions

| Name | Summary |
|---|---|
| [delay](../delay.md) | `fun <V> `[`Promise`](./index.md)`<`[`V`](../delay.md#V)`>.delay(msTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Promise`](./index.md)`<`[`V`](../delay.md#V)`>` |
| [then](../then.md) | `fun <V, X> `[`Promise`](./index.md)`<`[`Promise`](./index.md)`<`[`V`](../then.md#V)`>>.then(onResolved: `[`SuccessHandler`](../-success-handler.md)`<`[`V`](../then.md#V)`, `[`X`](../then.md#X)`>): `[`Promise`](./index.md)`<`[`X`](../then.md#X)`>` |
