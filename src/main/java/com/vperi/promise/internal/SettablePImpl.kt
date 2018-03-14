package com.vperi.promise.internal

import com.vperi.promise.Executor
import com.vperi.promise.Result
import java.util.concurrent.Future

class SettablePImpl<V>(executor: Executor<V>) : PImpl<V>() {
  private var execFuture: Future<*>

  internal fun resolve(value: V) {
    settled.set(Result.Value(value))
    execFuture.cancel(true)
  }

  internal fun reject(error: Throwable) {
    settled.set(Result.Error(error))
    execFuture.cancel(true)
    if (!hasHandlers) {
      onUnhandledException(error)
    }
  }

  override fun cancel() {
    execFuture.cancel(true)
    super.cancel()
  }

  init {
    execFuture = execService.submit({
      try {
        executor(this::resolve, this::reject)
      } catch (e: Exception) {
        reject(e.cause ?: e)
      }
    })
  }
}