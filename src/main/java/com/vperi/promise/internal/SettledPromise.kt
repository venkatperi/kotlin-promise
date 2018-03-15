package com.vperi.promise.internal

import com.vperi.promise.Result

class SettledPromise<V>(result: Result<V>) : AbstractPromise<V>() {

  internal constructor(value: V) : this(Result.Value(value))

  internal constructor(error: Throwable) : this(Result.Error(error))

  init {
    settled.set(result)
  }
}