package com.vperi.promise

class Defer<V> {
  private lateinit var resolver: ((V) -> Unit)

  private lateinit var rejector: ((Exception) -> Unit)

  val promise: P<V> = promise({ resolve, reject ->
    resolver = resolve
    rejector = reject
  })

  fun resolve(result: V) = resolver(result)

  fun reject(error: Exception) = rejector(error)
}


