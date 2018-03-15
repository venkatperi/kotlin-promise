package com.vperi.promise

import com.vperi.promise.internal.DeferImpl
import com.vperi.promise.internal.SettablePromise
import com.vperi.promise.internal.SettledPromise

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

//fun <V> List<Promise<V>>.allDone(): Promise<List<Result<V>>> = Promise.allDone(this)
//
//fun <V> List<Promise<V>>.all(): Promise<List<V>> = Promise.all(this)
//
//fun <V> List<Promise<V>>.race(): Promise<V> = Promise.race(this)

