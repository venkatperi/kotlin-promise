package com.vperi.promise.internal

import com.vperi.promise.Defer
import com.vperi.promise.P

class DeferImpl<V> : Defer<V> {

  override val promise: P<V> = SettablePImpl({ _, _ -> })

  override fun resolve(result: V) {
    (promise as SettablePImpl<V>).resolve(result)
  }

  override fun reject(error: Throwable) {
    (promise as SettablePImpl<V>).reject(error)
  }
}