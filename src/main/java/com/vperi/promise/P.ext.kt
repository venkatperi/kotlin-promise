package com.vperi.promise

import com.vperi.promise.internal.PImpl
import com.vperi.promise.internal.toKovenant
import com.vperi.promise.internal.wrap
import nl.komponents.kovenant.any

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
fun <V> promise(executor: Executor<V>): P<V> {
  return PImpl(executor)
}

/**
 * Returns a promise that is resolved with the given [value].
 */
fun <V> P.Companion.resolve(value: V): P<V> {
  return promise({ r, _ -> r(value) })
}

/**
 * Returns a Promise object that is rejected with the given reason.
 */
fun P.Companion.reject(reason: Exception): P<Unit> {
  return promise({ _, r -> r(reason) })
}

/**
 *  Returns a single Promise that resolves when all of the promises in the
 *  [Iterable] argument have resolved. It rejects with the reason of the
 *  first promise that rejects.
 */
fun <V> P.Companion.all(promises: Iterable<P<V>>): P<List<V>> =
  P.wrap(nl.komponents.kovenant.all(promises.map { it.toKovenant() }))

/**
 *  Returns a promise that resolves or rejects as soon as one of the promises
 *  in the [Iterable] resolves or rejects, with the value or reason from that promise.
 */
fun <V> P.Companion.race(promises: Iterable<P<V>>): P<V> =
  promise({ resolve, reject ->
    any(promises.map { it.toKovenant() })
      .success(resolve)
      .fail {
        val errors = when {
          it.size == 1 -> it[0]
          else -> AggregateException(it)
        }
        reject(errors)
      }
  })

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
//  PImpl({ resolve, reject ->
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
    this.then {
      it.then {
        try {
          resolve(onResolved(it))
        } catch (e: Exception) {
          reject(e)
        }
      }.catch(reject)
    }
  })
