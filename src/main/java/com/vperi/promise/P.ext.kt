package com.vperi.promise

import com.vperi.promise.internal.DeferImpl
import com.vperi.promise.internal.SettablePImpl
import com.vperi.promise.internal.SettledPimpl

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
fun <V> promise(executor: Executor<V>): P<V> = SettablePImpl(executor)

/**
 * Returns a already settled promise that resolves to [value]
 *
 * @param V Type of the promise's value
 * @param value the value of the promise
 */
fun <V> promise(value: V): P<V> = SettledPimpl(value)

/**
 * Returns a already settled promise that rejects with [error]
 *
 * @param V Type of the promise's value
 * @param error the reason of rejection
 * @returns P<V>
 */
fun <V> promise(error: Throwable): P<V> = SettledPimpl(error)

/**
 * Returns a [Defer] object which can be eventually settled
 *
 * @param V Type of the promise's value
 */
fun <V> deferred(): Defer<V> = DeferImpl()

@JvmName("pThen")
fun <V, X> P<P<V>>.then(onResolved: SuccessHandler<V, X>): P<X> =
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

fun <V> P<V>.delay(msTime: Long): P<V> =
  this.then {
    Thread.sleep(msTime)
    it
  }

fun <V> List<P<V>>.allDone(): P<List<Result<V>>> = P.allDone(this)

fun <V> List<P<V>>.all(): P<List<V>> = P.all(this)

fun <V> List<P<V>>.race(): P<V> = P.race(this)

