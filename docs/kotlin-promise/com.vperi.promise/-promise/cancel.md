[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [cancel](./cancel.md)

# cancel

`abstract fun cancel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Cancels a pending. cancel() has no effect if the promise
has already settled. Otherwise the promise will be rejected
with a [InterruptedException](http://docs.oracle.com/javase/6/docs/api/java/lang/InterruptedException.html).

