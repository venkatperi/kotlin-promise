package com.vperi.promise.internal

import com.vperi.promise.Executor
import com.vperi.promise.FailureHandler
import com.vperi.promise.P
import com.vperi.promise.SuccessHandler
import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task

class PKovenant<V> internal constructor(executor: Executor<V>) : P<V>() {
  private val defer = deferred<V, Exception>() {
    //on cancelled
    println("cancelled")
  }

  var hasHandlers: Boolean = false

  override val isSettled: Boolean
    get() = promise.isDone()

  internal val promise by lazy { defer.promise }

  init {
    val reject = { e: Exception ->
      when {
        !hasHandlers -> onUnhandledException(e)
        else -> defer.reject(e)
      }
    }
    task(context) {
      executor(defer::resolve, reject)
    }.fail(reject)
  }

  override fun cancel() {
    Kovenant.cancel(promise, Exception())
  }

  override fun <X> addHandler(onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>?): P<X> {
    hasHandlers = true
    return PKovenant({ resolve, reject ->
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

  companion object {
    val context = Kovenant.createContext {
      workerContext.dispatcher {
        name = "P"
        concurrentTasks = 16
      }
    }

  }
}

