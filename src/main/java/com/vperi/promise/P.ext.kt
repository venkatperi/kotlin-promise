package com.vperi.promise

import com.vperi.promise.internal.PImpl

/**
 * Returns a [P] promise
 *
 * @param executor A function that is passed with the arguments
 *    resolve and reject. The executor function is executed immediately,
 *    passing resolve and reject functions (the executor is called
 *    before the Promise constructor even returns the created object).
 *
 *    The resolve and reject functions, when called, resolve or reject
 *    the promise, respectively. The executor normally initiates some
 *    asynchronous work, and then, once that completes, either calls
 *    the resolve function to resolve the promise or else rejects it
 *    if an error occurred.
 *
 *    If an error is thrown in the executor function, the promise
 *    is rejected.
 */
fun <V> promise(executor: Executor<V>): P<V> = PImpl(executor)

fun <V> promise(value: V): P<V> = PImpl(value)

fun <V> promise(error: Throwable): P<V> = PImpl(error)

/**
 * Returns a Promise and deals with rejected cases only.
 *
 * If the original promise rejects, the handler [onRejected] is called with
 * the reason of rejection. The returned promise resolves with the value
 * returned by the handler so that the promise chain can continue. If the
 * handler throws, the returned promise rejects with that exception.
 *
 * If the original promise is fulfilled, the returned promise resolves to
 * the original's fulfillment value.
 */
//fun <V> P<V>.catch(onRejected: FailureHandler<*>): P<V> =
//  PKovenant({ resolve, reject ->
//    this.finally {
//      when (it) {
//        is Result.Value -> resolve(it.value)
//        is Result.Error -> {
//          try {
//            resolve(onRejected(it.error) as V)
//          } catch (e: Exception) {
//            reject(e)
//          }
//        }
//      }
//    }
//  })

/**
 * Special case to convert [Nothing] to [Unit]
 */
//@JvmName("nothingCatch")
//fun <X> P<Nothing>.catch(onRejected: FailureHandler<X>): P<X> =
//  promise({ resolve, reject ->
//    this.finally {
//      when (it) {
//       is Result.Value -> // shouldn't get here
//        is Result.Error -> {
//          try {
//            resolve(onRejected(it.error))
//          } catch (e: Exception) {
//            reject(e)
//          }
//        }
//      }
//    }
//  })

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
