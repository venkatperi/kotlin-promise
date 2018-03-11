package com.vperi.promise

import com.google.common.collect.ImmutableList
import com.vperi.kotlin.Event
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value.
 *
 */
abstract class P<V> {
  fun then(): P<Unit> =
    addHandler({})

  fun <X> then(onResolved: SuccessHandler<V, X>): P<X> =
    addHandler(onResolved)

  fun <X> then(
    onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>): P<X> =
    addHandler(onResolved, onRejected)

  fun catch(onRejected: FailureHandler<V>): P<V> =
    addHandler({ it }, onRejected)

  fun <X> finally(handler: (Result<V>) -> X): P<X> =
    addHandler(
      { handler(Result.Value(it)) },
      { handler(Result.Error(it)) })

  protected abstract val isSettled: Boolean

  protected abstract fun cancel()

  protected abstract fun <X> addHandler(
    onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>? = null): P<X>

  companion object {
    @JvmStatic
    fun <V> resolve(value: V): P<V> = promise(value)

    @JvmStatic
    fun reject(reason: Throwable): P<Unit> = promise(reason)

    @JvmStatic
    @JvmName("rejectWithType")
    fun <V> reject(reason: Throwable): P<V> = promise(reason)

    @JvmStatic
    fun <V> race(promises: List<P<V>>): P<V> =
      promise({ res, rej ->
        ImmutableList.copyOf(promises).forEach {
          it.finally {
            if (!isSettled) {
              when (it) {
                is Result.Value -> res(it.value)
                is Result.Error -> rej(it.error)
              }
            }
          }
        }
      })

    @JvmStatic
    fun <V> all(promises: List<P<V>>): P<List<V>> {
      val results = HashMap<Int, V>()
      val done = AtomicInteger(0)
      return promise({ res, rej ->
        val items = ImmutableList.copyOf(promises)

        val cancelAndReject = { e: Throwable ->
          items.forEach {
            if (!it.isSettled)
              it.cancel()
          }
          rej(e)
        }

        items.forEachIndexed { index: Int, item: P<V> ->
          item.then {
            if (!isSettled) {
              val completed = done.incrementAndGet()
              results[index] = it
              if (completed >= items.size) {
                res((0 until items.size).map { results[it]!! })
              }
            }
          }.catch {
            cancelAndReject(it)
          }
        }
      })
    }

    val onUnhandledException = Event<Throwable>()
  }
}
