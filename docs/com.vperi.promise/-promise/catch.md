[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [catch](./catch.md)

# catch

`fun catch(onRejected: `[`FailureHandler`](../-failure-handler.md)`<`[`V`](index.md#V)`>): `[`Promise`](index.md)`<`[`V`](index.md#V)`>`

Returns a pending promise which handles rejection cases only.
If the original promise rejects, [onRejected](catch.md#com.vperi.promise.Promise$catch(kotlin.Function1((kotlin.Throwable, com.vperi.promise.Promise.V)))/onRejected) is called with the
reason of rejection.

### Parameters

`onRejected` - handler called if the current promise is
rejected. This function has a single argument, the [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)
reason of rejection.

**Return**
a pending promise.

