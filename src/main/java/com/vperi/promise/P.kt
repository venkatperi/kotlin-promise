package com.vperi.promise

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value.
 *
 */
interface P<V> {
  fun then(): P<Unit>

  fun <X> then(onResolved: SuccessHandler<V, X>): P<X>

  fun <X> then(
    onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>): P<X>

  /**
   * Returns a Promise and deals with rejected cases only.
   *
   * If the original promise rejects, the handler [onRejected] is called with
   * the reason of rejection. The returned promise resolves with the value
   * returned by the handler so that the promise chain can continue. If the
   * handler throws, the returned promise rejects with that exception.
   *
   * If the original promise is fulfilled, the returned promise resolves to
   * the original's fulfillment value.
   */
  fun catch(onRejected: FailureHandler<V>): P<V>

  fun <X> finally(handler: (Result<V>) -> X): P<X>

  companion object
}
