package com.vperi.promise.internal

import com.vperi.promise.P
import com.vperi.promise.Result
import com.vperi.promise.promise
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred

/**
 * Created by venkat on 3/4/18.
 */
internal fun <V> P<V>.toKovenant(): Promise<V, Exception> {
  val defer = deferred<V, Exception>()
  this.finally {
    when (it) {
      is Result.Value -> defer.resolve(it.value)
      is Result.Error -> defer.reject(it.error)
    }
  }
  return defer.promise
}

fun <V> P.Companion.wrap(p: Promise<V, Exception>): P<V> =
  promise({ resolve, reject ->
    p.success(resolve).fail(reject)
  })
