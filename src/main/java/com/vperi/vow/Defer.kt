package com.vperi.vow

import nl.komponents.kovenant.deferred

class Defer<V> {

  private val defer = deferred<V, Exception>()

  val promise by lazy { Vow.of(defer.promise) }

  fun resolve(result: V) {
    defer.resolve(result)
  }

  fun reject(error: Exception) {
    defer.reject(error)
  }
}


