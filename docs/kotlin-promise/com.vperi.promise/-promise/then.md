[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [then](./then.md)

# then

`fun <X> then(onResolved: `[`SuccessHandler`](../-success-handler.md)`<`[`V`](index.md#V)`, `[`X`](then.md#X)`>, onRejected: `[`FailureHandler`](../-failure-handler.md)`<`[`X`](then.md#X)`>): `[`Promise`](index.md)`<`[`X`](then.md#X)`>`

Returns a pending promise. When the current promise settles,
the handler function, either [onResolved](then.md#com.vperi.promise.Promise$then(kotlin.Function1((com.vperi.promise.Promise.V, com.vperi.promise.Promise.then.X)), kotlin.Function1((kotlin.Throwable, com.vperi.promise.Promise.then.X)))/onResolved) or [onRejected](then.md#com.vperi.promise.Promise$then(kotlin.Function1((com.vperi.promise.Promise.V, com.vperi.promise.Promise.then.X)), kotlin.Function1((kotlin.Throwable, com.vperi.promise.Promise.then.X)))/onRejected), gets
called asynchronously with the settlement value if the current
promise resolves, or the reason if the current promise rejects.

If the handler function:

* returns a value, the returned promise gets resolved with the
returned value as its value;
* throws an error, the promise returned by then() gets rejected
with the exception as the reason;

### Parameters

`onResolved` - handler called if the current promise is
resolved. This function has a single argument, the fulfillment value

`onRejected` - handler called if the current promise is
rejected. This function has a single argument, the [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)
reason of rejection.

**Return**
Promise

`fun <X> then(onResolved: `[`SuccessHandler`](../-success-handler.md)`<`[`V`](index.md#V)`, `[`X`](then.md#X)`>): `[`Promise`](index.md)`<`[`X`](then.md#X)`>`

Returns a pending promise. Accepts a single handler for fulfillment.
If the original promise rejects, the returned promise rejects
with the original's reason of rejection.

This overload is best suited for chaining promises.

### Parameters

`onResolved` - handler called if the current promise is
resolved. This function has a single argument, the fulfillment value

**Return**
a pending promise.

`fun then(): `[`Promise`](index.md)`<`[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`>`

Returns a pending promise. When the current promise settles,
the returned promise enters the same settlement state. However, the
fulfillment value of the original promise is not propagated.

This overload is useful if all we want to know is when a promise
settles, but don't care about its value.

**Return**
A pending promise which resolves to [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

