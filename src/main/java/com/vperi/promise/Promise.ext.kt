package com.vperi.promise

import com.vperi.promise.internal.DeferImpl
import com.vperi.promise.internal.SettablePromise
import com.vperi.promise.internal.SettledPromise
import java.util.concurrent.Callable

/**
 * Returns a promise
 *
 * @param V Type of the promise's value
 *
 * @param executor A function that is passed with the arguments
 *    resolve and reject. The execService function is executed immediately,
 *    passing resolve and reject functions (the execService is called
 *    before the Promise constructor even returns the created object).
 *
 *    The resolve and reject functions, when called, resolve or reject
 *    the promise, respectively. The execService normally initiates some
 *    asynchronous work, and then, once that completes, either calls
 *    the resolve function to resolve the promise or else rejects it
 *    if an error occurred.
 *
 *    If an error is thrown in the execService function, the promise
 *    is rejected.
 *
 */
fun <V> promise(executor: Executor<V>): Promise<V> = SettablePromise(executor)

/**
 * Returns a already settled promise that resolves to [value]
 *
 * @param V Type of the promise's value
 * @param value the value of the promise
 */
fun <V> promise(value: V): Promise<V> = SettledPromise(value)

/**
 * Returns a already settled promise that rejects with [error]
 *
 * @param V Type of the promise's value
 * @param error the reason of rejection
 * @returns Promise<V>
 */
fun <V> promise(error: Throwable): Promise<V> = SettledPromise(error)

/**
 * Convenience method. Returns a promise which resolves when the
 * [Runnable] finishes. The Runnable is executed asynchronously.
 * If the Runnable throws, the returned promise rejects with the reason.
 *
 * @param block [Runnable] to execute.
 * @return Promise<Unit>
 */
fun promise(block: Runnable): Promise<Unit> =
  promise { resolve, _ ->
    block.run()
    resolve(Unit)
  }

/**
 * Convenience method. Returns a promise which resolves when the
 * [Callable] finishes with the value returned by the Callable.
 * The Callable is executed asynchronously.  If the Callable throws,
 * the returned promise rejects with the reason.
 *
 * @param V type of the promise.
 * @param block [Callable] to execute.
 * @return Promise<V>
 */
fun <V> promise(block: Callable<V>): Promise<V> =
  promise { resolve, _ ->
    resolve(block.call())
  }

/**
 * Returns a [Defer] object which can be eventually settled
 *
 * @param V Type of the promise's value
 */
fun <V> deferred(): Defer<V> = DeferImpl()

@JvmName("pThen")
fun <V, X> Promise<Promise<V>>.then(onResolved: SuccessHandler<V, X>): Promise<X> =
  promise({ resolve, reject ->
    this@then.then {
      it.then {
        try {
          resolve(onResolved(it))
        } catch (e: Exception) {
          reject(e)
        }
      }.catch(reject)
    }
  })

fun <V> Promise<V>.delay(msTime: Long): Promise<V> =
  this.then {
    Thread.sleep(msTime)
    it
  }

