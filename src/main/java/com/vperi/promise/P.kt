package com.vperi.promise

import com.google.common.collect.ImmutableList
import com.vperi.kotlin.Event
import java.util.concurrent.CountDownLatch

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value.
 *
 */
abstract class P<V> {
  /**
   * Returns a pending promise. When the current promise settles,
   * the handler function, either [onResolved] or [onRejected], gets
   * called asynchronously with the settlement value if the current
   * promise resolves, or the reason if the current promise rejects.
   *
   * If the handler function:
   *
   * - returns a value, the returned promise gets resolved with the
   * returned value as its value;
   * - throws an error, the promise returned by then() gets rejected
   * with the exception as the reason;
   *
   * @param onResolved handler called if the current promise is
   * resolved. This function has a single argument, the fulfillment value
   *
   * @param onRejected handler called if the current promise is
   * rejected. This function has a single argument, the [Throwable]
   * reason of rejection.
   *
   * @return Promise
   */
  fun <X> then(
    onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>): P<X> =
    addHandler(onResolved, onRejected)

  /**
   * Returns a pending promise. Accepts a single handler for fulfillment.
   * If the original promise rejects, the returned promise rejects
   * with the original's reason of rejection.
   *
   * This overload is best suited for chaining promises.
   *
   * @param onResolved handler called if the current promise is
   * resolved. This function has a single argument, the fulfillment value
   *
   * @return a pending promise.
   */
  fun <X> then(onResolved: SuccessHandler<V, X>): P<X> =
    addHandler(onResolved)

  /**
   * Returns a pending promise. When the current promise settles,
   * the returned promise enters the same settlement state. However, the
   * fulfillment value of the original promise is not propagated.
   *
   * This overload is useful if all we want to know is when a promise
   * settles, but don't care about its value.
   *
   * @return A pending promise which resolves to [Unit]
   */
  fun then(): P<Unit> =
    addHandler({})

  /**
   * Returns a pending promise which handles rejection cases only.
   * If the original promise rejects, [onRejected] is called with the
   * reason of rejection.
   *
   * @param onRejected handler called if the current promise is
   * rejected. This function has a single argument, the [Throwable]
   * reason of rejection.
   *
   * @return a pending promise.
   */
  fun catch(onRejected: FailureHandler<V>): P<V> =
    addHandler({ it }, onRejected)

  /**
   * Returns a pending promise. The single argument [handler] is called
   * when the promise settles with either the fulfillment value or
   * rejection reason.
   *
   * @param handler Accepts a single argument [Result] which
   * represents the settlement value [Result.Value] or
   * rejection reason [Result.Error]
   *
   */
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
          it.then(res).catch(rej)
        }
      })

    @JvmStatic
    fun <V> all(promises: List<P<V>>): P<List<V>> {
      val items = ImmutableList.copyOf(promises)
      val results = HashMap<Int, V>()
      val latch = CountDownLatch(items.size)

      return promise({ res, rej ->
        items.forEachIndexed { index, item ->
          item.then {
            results[index] = it
          }.catch {
            rej(it)
          }.finally {
            latch.countDown()
          }
        }

        latch.await()
        if (!isSettled)
          res((0 until items.size).map { results[it]!! })
      })
    }

    @JvmStatic
    val onUnhandledException = Event<Throwable>()
  }
}
