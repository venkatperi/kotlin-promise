[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [finally](./finally.md)

# finally

`fun <X> finally(handler: (`[`Result`](../-result/index.md)`<`[`V`](index.md#V)`>) -> `[`X`](finally.md#X)`): `[`Promise`](index.md)`<`[`X`](finally.md#X)`>`

Returns a pending promise. The single argument [handler](finally.md#com.vperi.promise.Promise$finally(kotlin.Function1((com.vperi.promise.Result((com.vperi.promise.Promise.V)), com.vperi.promise.Promise.finally.X)))/handler) is called
when the promise settles with either the fulfillment value or
rejection reason.

### Parameters

`handler` - Accepts a single argument [Result](../-result/index.md) which
represents the settlement value [Result.Value](../-result/-value/index.md) or
rejection reason [Result.Error](../-result/-error/index.md)