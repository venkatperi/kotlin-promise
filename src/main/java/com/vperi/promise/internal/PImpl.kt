package com.vperi.promise.internal

import com.vperi.promise.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class PImpl<V> internal constructor(
  executor: Executor<V>? = null,
  var result: Result<V>? = null,
  fut: CompletableFuture<V>? = null) : P<V>() {

  private val timing = Timing()
  val id: Int = nextId.getAndIncrement()

  private var future: CompletableFuture<V>? = null
  val resultFuture: CompletableFuture<V>
  private val settled: CompletableFuture<Result<V>>

  override val isSettled: Boolean
    get() = settled.isDone

  constructor(value: V) : this(result = Result.Value(value))

  constructor(error: Throwable) : this(result = Result.Error(error))

  constructor(f: CompletableFuture<V>) : this(null, null, f)

  init {
    settled = when {
      result != null -> CompletableFuture.completedFuture(result)
      else -> {
        future = fut ?: CompletableFuture()
        future!!.handleAsync { value: V?, error: Throwable? ->
          when {
            error != null -> Result.Error<V>(error.cause ?: error)
            else -> Result.Value(value!!)
          }
        }
      }
    }

    resultFuture = settled.thenApplyAsync {
      when (it) {
        is Result.Value -> it.value
        is Result.Error -> throw it.error
      }
    }

    if (executor != null) {
      if (result != null)
        throw IllegalArgumentException("Can't set both of executor and result")

      CompletableFuture.runAsync {
        timing.mark("executor")
        executor(this::resolve, this::reject)
      }.handle { _, e: Throwable? ->
        if (e != null)
          reject(e.cause ?: e)
      }
    }
  }

  fun resolve(value: V) {
    future?.complete(value)
  }

  fun reject(error: Throwable) {
    future?.completeExceptionally(error)
    if (settled.numberOfDependents <= 1) {
      onUnhandledException(error)
    }
  }

  override fun cancel() {
    future?.cancel(true)
    settled.cancel(true)
  }

  override fun <X> addHandler(onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>?): P<X> {
    val p = PImpl<X>()
    settled.thenAcceptAsync { result ->
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
    return p
  }

  override fun toString(): String {
    val params = HashMap<String, String>()
    params["result"] = when {
      !isSettled -> "pending"
      else -> result.toString()
    }
    params["id"] = id.toString()
    return params.toSortedMap().toString()
  }

  companion object {
    var nextId = AtomicInteger(0)
  }

}


