package com.vperi.promise.internal

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.SettableFuture
import com.vperi.promise.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

abstract class PImpl<V> internal constructor() : P<V>() {
  private val children = ArrayList<PImpl<*>>()
  private val id: Int = nextId.getAndIncrement()

  val settled = SettableFuture.create<Result<V>>()!!

  override val isDone: Boolean
    get() = settled.isDone

  override fun cancel() {
    settled.cancel(true)
  }

  protected var hasHandlers: Boolean = false

  fun walk(visitor: PImpl<*>.() -> Unit) {
    children.forEach {
      it.visitor()
      it.walk(visitor)
    }
  }

  override fun <X> addHandler(onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>?): P<X> {
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

    children += p.promise as PImpl<*>
    return p.promise
  }

  val execService: ExecutorService
    get() = Companion.execService

  companion object {
    var nextId = AtomicInteger(0)

    private val threadCount = AtomicInteger(0)

    private val threadFactory = ThreadFactory {
      Executors.defaultThreadFactory().newThread(it).apply {
        name = "${nextId.get() - 1}/${threadCount.getAndIncrement()}"
      }
    }

//    var execService = Executors.newCachedThreadPool()!!
    var execService = Executors.newFixedThreadPool(100)!!
  }
}

