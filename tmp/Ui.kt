package com.vperi.promise

import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi

fun <V, X> P<V>.thenUi(handler: (V) -> X): P<X> = P({ resolve, reject ->
  val handle = { x: V ->
    try {
      resolve(handler(x))
    } catch (e: Exception) {
      reject(e)
    }
  }
  promise.successUi { handle(it) }.failUi { reject(it) }
})

fun <X> P<*>.catchUi(handler: (Exception) -> X): P<X> = P({ resolve, reject ->
  promise.failUi {
    try {
      resolve(handler(it))
    } catch (e: Exception) {
      reject(e)
    }
  }
})

fun <X> P<*>.finallyUi(handler: (Any?) -> X): P<X> = P({ resolve, reject ->
  val handle = { x: Any? ->
    try {
      resolve(handler(x))
    } catch (e: Exception) {
      reject(e)
    }
  }
  promise.successUi { handle(it) }.failUi { handle(it) }
})

