[kotlin-promise](../index.md) / [com.vperi.promise](index.md) / [promise](./promise.md)

# promise

`fun <V> promise(executor: `[`Executor`](-executor.md)`<`[`V`](promise.md#V)`>): `[`Promise`](-promise/index.md)`<`[`V`](promise.md#V)`>`

Returns a promise

### Parameters

`V` - Type of the promise's value

`executor` - A function that is passed with the arguments
    resolve and reject. The execService function is executed immediately,
    passing resolve and reject functions (the execService is called
    before the Promise constructor even returns the created object).`fun <V> promise(value: `[`V`](promise.md#V)`): `[`Promise`](-promise/index.md)`<`[`V`](promise.md#V)`>`

Returns a already settled promise that resolves to [value](promise.md#com.vperi.promise$promise(com.vperi.promise.promise.V)/value)

### Parameters

`V` - Type of the promise's value

`value` - the value of the promise`fun <V> promise(error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](-promise/index.md)`<`[`V`](promise.md#V)`>`

Returns a already settled promise that rejects with [error](promise.md#com.vperi.promise$promise(kotlin.Throwable)/error)

### Parameters

`V` - Type of the promise's value

`error` - the reason of rejection

**Returns**
Promise

