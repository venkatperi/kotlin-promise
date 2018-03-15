[kotlin-promise](../../index.md) / [com.vperi.promise](../index.md) / [Promise](index.md) / [catchX](./catch-x.md)

# catchX

`fun <X> catchX(onRejected: `[`FailureHandler`](../-failure-handler.md)`<`[`X`](catch-x.md#X)`?>): `[`Promise`](index.md)`<`[`X`](catch-x.md#X)`?>`

Like [catch](catch.md), catchX() handles rejection cases for the current promise.
Where it differs is that it allows the failure handler to return
an arbitrary type, and does not propagate the current promise's value on
success.  If the current promise succeeds, catchX() will
attempt to cast the fulfillment value to the new type and return that,
or null.

