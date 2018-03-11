package com.vperi.promise.internal

import com.vperi.promise.FailureHandler
import com.vperi.promise.P.Companion.reject
import com.vperi.promise.Result
import com.vperi.promise.SuccessHandler

data class Handler<in V, X>(
  private val onResolved: SuccessHandler<V, X>,
  private val onRejected: FailureHandler<X>?,
  val promise: PImpl<X>) {

  operator fun invoke(result: Result<V>) {
    try {
      when (result) {
        is Result.Value -> promise.resolve(onResolved(result.value))
        is Result.Error -> {
          if (onRejected != null)
            promise.resolve(onRejected.invoke(result.error))
          else
            reject(result.error)
        }
      }
    } catch (e: Exception) {
      reject(e)
    }
  }
}