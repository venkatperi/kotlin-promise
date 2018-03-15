@file:Suppress("unused")

package com.vperi.promise

import com.google.common.collect.ImmutableList
import com.vperi.kotlin.Event
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value (or error).
 *
 * Creating Promises:
 * @see promise
 */
abstract class Promise<V> {
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
    onRejected: FailureHandler<X>): Promise<X> =
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
  fun <X> then(onResolved: SuccessHandler<V, X>): Promise<X> =
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
  fun then(): Promise<Unit> =
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
  fun catch(onRejected: FailureHandler<V>): Promise<V> {
    return addHandler({ it }, onRejected)
  }

  /**
   * Like [catch], catchX() handles rejection cases for the current promise.
   * Where it differs is that it allows the failure handler to return
   * an arbitrary type, and does not propagate the current promise's value on
   * success.  If the current promise succeeds, catchX() will
   * attempt to cast the fulfillment value to the new type and return that,
   * or null.
   */
  fun <X> catchX(onRejected: FailureHandler<X?>): Promise<X?> =
    addHandler({ null }, onRejected)

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
  fun <X> finally(handler: (Result<V>) -> X): Promise<X> =
    addHandler(
      { handler(Result.Value(it)) },
      { handler(Result.Error(it)) })

  /**
   * True if the promise has settled.
   */
  abstract val isDone: Boolean

  /**
   * Cancels a pending. cancel() has no effect if the promise
   * has already settled. Otherwise the promise will be rejected
   * with a [InterruptedException].
   */
  abstract fun cancel()

  internal abstract fun <X> addHandler(
    onResolved: SuccessHandler<V, X>,
    onRejected: FailureHandler<X>? = null): Promise<X>

  companion object {
    /**
     * Created a already settled promise in the resolved state.
     *
     * @param value the value of fulfillment
     */
    @JvmStatic
    fun <V> resolve(value: V): Promise<V> = promise(value)

    /**
     * Create a already settled promise in the rejected state
     *
     * @param reason the reason of rejection
     *
     * @returns Promise<Unit>
     */
    @JvmStatic
    fun reject(reason: Throwable): Promise<Unit> = promise(reason)

    /**
     * Create a already settled promise in the rejected state
     *
     * @param V the type of the promise's value
     * @param reason the reason of rejection
     *
     * @returns Promise<V>
     */
    @JvmStatic
    @JvmName("rejectWithType")
    fun <V> reject(reason: Throwable): Promise<V> = promise(reason)

    /**
     * Returns a promise that resolves or rejects as soon as
     * one of the promises in the iterable resolves or rejects,
     * with the value or reason from that promise.
     *
     * @param promises [Iterable] list of promises.
     *
     * @return A pending Promise that resolves or rejects
     * asynchronically as soon as one of the promises in the given
     * iterable resolves or rejects, adopting that first promise's
     * value as its value.
     *
     * If the iterable passed is empty, the promise returned will be forever
     * pending.
     */
    @JvmStatic
    fun <V> race(promises: List<Promise<V>>): Promise<V> =
      promise({ res, rej ->
        ImmutableList.copyOf(promises).forEach {
          it.then(res).catch(rej)
        }
      })

    /**
     *  Returns a single Promise that resolves when all of the promises
     *  in the iterable argument have resolved or when the iterable argument
     *  contains no promises. It rejects immediately with the reason of the
     *  first promise that rejects without waiting for the other promises
     *  to settle.
     *
     * @param promises [Iterable] list of promises.
     *
     * @return Promise which resolves to a [List] of all resolved values
     *   from the supplied list of [promises], if all promises resolve,
     *   or assumes the reason of rejection of the first promise that rejects.
     */
    @JvmStatic
    fun <V> all(promises: List<Promise<V>>): Promise<List<V>> {
      val items = ImmutableList.copyOf(promises)
      val results = ConcurrentHashMap<Int, V>()
      val latch = CountDownLatch(items.size)

      return promise({ res, rej ->
        items.forEachIndexed { index, item ->
          item.finally {
            when (it) {
              is Result.Value -> results[index] = it.value
              is Result.Error -> rej(it.error)
            }
            latch.countDown()
          }
        }

        latch.await()
        if (!isDone)
          res((0 until items.size).map { results[it]!! })
      })
    }

    @JvmStatic
    fun <V> allDone(promises: List<Promise<V>>): Promise<List<Result<V>>> {
      val items = ImmutableList.copyOf(promises)
      val results = ConcurrentHashMap<Int, Result<V>>()
      val latch = CountDownLatch(items.size)

      return promise({ res, _ ->
        items.forEachIndexed { index, item ->
          item.finally {
            results[index] = it
            latch.countDown()
          }
        }

        latch.await()
        res((0 until items.size).map { results[it]!! })
      })
    }

    var config: Configuration = defaultConfig()

    @JvmStatic
    val executorService: ExecutorService
      get() = config.executorService

    @JvmStatic
    val onUnhandledException = Event<Throwable>()

    private fun defaultConfig(): Configuration {
      return Configuration(
        executorService = Executors.newCachedThreadPool()
      )
    }
  }
}

