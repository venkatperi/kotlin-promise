package com.vperi.promise.internal

import com.google.common.util.concurrent.SettableFuture
import com.vperi.promise.Result

class SettledPimpl<V>(result: Result<V>) : PImpl<V>() {


  internal constructor(value: V) : this(Result.Value(value))

  internal constructor(error: Throwable) : this(Result.Error(error))

  init {
    settled.set(result)
  }
}