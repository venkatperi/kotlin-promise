package com.vperi.promise.internal

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.SettableFuture
import com.vperi.promise.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractPromise<V> internal constructor() : Promise<V>() {
  private val children = ArrayList<AbstractPromise<*>>()
  private val id: Int = nextId.getAndIncrement()

  val settled = SettableFuture.create<Result<V>>()!!

  override val isDone: Boolean
    get() = settled.isDone

  override fun cancel() {
    settled.cancel(true)
  }

  protected var hasHandlers: Boolean = false

  fun walk(visitor: AbstractPromise<*>.() -> Unit) {
    children.forEach {
      it.visitor()
      it.walk(visitor)
    }
  }

  override fun <X> addHandler(onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>?): Promise<X> {
    hasHandlers = true
    val p = deferred<X>()

    settled.addCallback(object : FutureCallback<Result<V>> {
      override fun onSuccess(result: Result<V>?) {
        try {
          when (result) {
            is Result.Value -> p.resolve(onResolved(result.value))
            is Result.Error -> {
              if (onRejected != null)
                p.resolve(onRejected.invoke(result.error))
              else
                p.reject(result.error)
            }
          }
        } catch (e: Exception) {
          p.reject(e)
        }
      }

      override fun onFailure(t: Throwable?) {
        p.reject(t!!)
      }
    }, execService)

    children += p.promise as AbstractPromise<*>
    return p.promise
  }

  val execService: ExecutorService
    get() = Promise.executorService

  companion object {
    var nextId = AtomicInteger(0)
  }
}

