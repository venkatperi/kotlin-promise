package com.vperi.promise

import java.util.concurrent.Future

fun <V> Future<V>.toPromise(): Promise<V> {
  return promise { resolve, reject ->
    try {
      resolve(get())
    } catch (e: Exception) {
      reject(e.cause ?: e)
    }
  }
}
