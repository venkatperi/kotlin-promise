package com.vperi.promise

import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task

class PImpl<V> internal constructor(executor: Executor<V>) : P<V> {

  private val defer = deferred<V, Exception>()

  internal val promise by lazy { defer.promise }

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

  private fun <X> doThen(onResolved: SuccessHandler<V, X>): P<X> =
    PImpl({ resolve, reject ->
      promise.success {
        try {
          resolve(onResolved(it))
        } catch (e: Exception) {
          reject(e)
        }
      }.fail {
        reject(it)
      }
    })

  override fun then(): P<Unit> =
    doThen {}

  override fun <X> then(onResolved: SuccessHandler<V, X>): P<X> =
    doThen(onResolved)

  override fun <X> then(
    onRejected: FailureHandler<X>,
    onResolved: SuccessHandler<V, X>): P<X> =
    PImpl({ resolve, reject ->
      promise.success {
        try {
          resolve(onResolved(it))
        } catch (e: Exception) {
          reject(e)
        }
      }.fail {
        try {
          resolve(onRejected(it))
        } catch (e: Exception) {
          reject(e)
        }
      }
    })

//  override fun <X : V> catch(onRejected: FailureHandler<X>): P<X> =
//      PImpl({ resolve, reject ->
//        promise.fail {
//          try {
//            resolve(onRejected(it))
//          } catch (e: Exception) {
//            reject(e)
//          }
//        }.success {
//          resolve(it as X)
//        }
//      })

  override fun <X> finally(handler: (Result<V>) -> X): P<X> =
    PImpl({ resolve, reject ->
      val handle = { x: Result<V> ->
        try {
          resolve(handler(x))
        } catch (e: Exception) {
          reject(e)
        }
      }
      promise
        .success { handle(Result.Value(it)) }
        .fail { handle(Result.Error(it)) }
    })

}

