[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [reject](./reject.md)

# reject

`@JvmStatic fun reject(reason: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](index.md)`<`[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`>`

Create a already settled promise in the rejected state

### Parameters

`reason` - the reason of rejection

**Returns**
Promise

`@JvmStatic @JvmName("rejectWithType") fun <V> reject(reason: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](index.md)`<`[`V`](reject.md#V)`>`

Create a already settled promise in the rejected state

### Parameters

`V` - the type of the promise's value

`reason` - the reason of rejection

**Returns**
Promise

