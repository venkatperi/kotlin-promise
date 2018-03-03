package com.vperi.vow

typealias P<T> = Vow<T>

/**
 * Returns a pending [Vow].
 *
 * When the current promise settles, the supplied [handler] is called with the
 * result if the current promise resolves. If the [handler] function:
 *    - returns a value, the promise returned by [then] gets resolved
 *        with the returned value as its value;
 *    - throws an error, the promise returned by then gets rejected
 *        with the thrown error as its value;
 *
 */

fun <V, X> Vow<V>.then(handler: (V) -> X): Vow<X> = Vow({ resolve, reject ->
  val handle = { x: V ->
    try {
      resolve(handler(x))
    } catch (e: Exception) {
      reject(e)
    }
  }
  promise.success { handle(it) }.fail { reject(it) }
})

fun <V, X> Vow<V>.thenP(handler: (V) -> Vow<X>): Vow<X> = Vow({ resolve, reject ->
  promise.success {
    try {
      handler(it).then { resolve(it) }.catch { reject(it) }
    } catch (e: Exception) {
      reject(e)
    }
  }.fail { reject(it) }
})

/**
 * Returns a Promise and deals with rejected cases only.
 */
fun <V, X> Vow<V>.catch(handler: (Exception) -> X): Vow<X> = Vow({ resolve, reject ->
  promise.fail {
    try {
      resolve(handler(it))
    } catch (e: Exception) {
      reject(e)
    }
  }
})

/**
 * Returns a new [Vow] which is resolved when the original
 * promise is resolved. The handler is called when the promise
 * is settled, whether fulfilled or rejected.
 *
 * If the handler throws an exception, the returned promise is rejected
 * with the reason.
 */
fun <V, X> Vow<V>.finally(handler: (Any?) -> X): Vow<X> = Vow({ resolve, reject ->
  val handle = { x: Any? ->
    try {
      resolve(handler(x))
    } catch (e: Exception) {
      reject(e)
    }
  }
  promise.success { handle(it) }.fail { handle(it) }
})

