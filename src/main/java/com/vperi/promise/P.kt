package com.vperi.promise

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value.
 *
 */
interface P<V> {
  fun then(): P<Unit>

  fun <X> then(onResolved: SuccessHandler<V, X>): P<X>

  fun <X> then(onRejected: FailureHandler<X>,
    onResolved: SuccessHandler<V, X>): P<X>

//  fun <X> catch(onRejected: FailureHandler<X>): P<X>

  fun <X> finally(handler: (Result<V>) -> X): P<X>

  companion object
}
