package com.vperi.promise.internal

import com.vperi.promise.Defer
import com.vperi.promise.Promise

class DeferImpl<V> : Defer<V> {

  override val promise: Promise<V> = SettablePromise({ _, _ -> })

  override fun resolve(result: V) {
    (promise as SettablePromise<V>).resolve(result)
  }

  override fun reject(error: Throwable) {
    (promise as SettablePromise<V>).reject(error)
  }
}