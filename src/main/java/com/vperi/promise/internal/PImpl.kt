package com.vperi.promise.internal

import com.vperi.promise.*
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task

class PImpl<V> internal constructor(executor: Executor<V>) : P<V> {

  private val defer = deferred<V, Exception>()

  internal val promise by lazy { defer.promise }

  override fun then(): P<Unit> =
    doThen({})

  override fun <X> then(onResolved: SuccessHandler<V, X>): P<X> =
    doThen(onResolved)

  override fun <X> then(onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>): P<X> =
    doThen(onResolved, onRejected)

  override fun catch(onRejected: FailureHandler<V>): P<V> =
    doThen({ it }, onRejected)

  override fun <X> finally(handler: (Result<V>) -> X): P<X> =
    doThen({ handler(Result.Value(it)) },
      { handler(Result.Error(it)) })

  init {
    task {
      try {
        executor(defer::resolve, defer::reject)
      } catch (e: Exception) {
        defer.reject(e)
      }
    }.fail {
      defer.reject(it)
    }
  }

  private fun <X> doThen(onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>? = null): P<X> =
    PImpl({ resolve, reject ->
      promise.success {
        try {
          resolve(onResolved(it))
        } catch (e: Exception) {
          reject(e)
        }
      }.fail {
        if (onRejected != null) {
          try {
            resolve(onRejected(it))
          } catch (e: Exception) {
            reject(e)
          }
        } else
          reject(it)
      }
    })
}

