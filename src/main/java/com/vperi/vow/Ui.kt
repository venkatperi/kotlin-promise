package com.vperi.vow

import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi

fun <V, X> Vow<V>.thenUi(handler: (V) -> X): Vow<X> = Vow({ resolve, reject ->
  val handle = { x: V ->
    try {
      resolve(handler(x))
    } catch (e: Exception) {
      reject(e)
    }
  }
  promise.successUi { handle(it) }.failUi { reject(it) }
})

fun <V, X> Vow<V>.catchUi(handler: (Exception) -> X): Vow<X> = Vow({ resolve, reject ->
  promise.failUi {
    try {
      resolve(handler(it))
    } catch (e: Exception) {
      reject(e)
    }
  }
})

fun <V, X> Vow<V>.finallyUi(handler: (Any?) -> X): Vow<X> = Vow({ resolve, reject ->
  val handle = { x: Any? ->
    try {
      resolve(handler(x))
    } catch (e: Exception) {
      reject(e)
    }
  }
  promise.successUi { handle(it) }.failUi { handle(it) }
})
