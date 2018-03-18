package com.vperi.promise

import java.util.concurrent.Future

/**
 * Returns a [Promise] for the [Future] which resolves or rejects
 * when the future completes or throws.
 *
 * @receiver [Future]
 * @return Promise<V> which resolves with the value of the future
 * or if the future throws an exception, the reason of rejection
 */
fun <V> Future<V>.toPromise(): Promise<V> {
  return promise { resolve, reject ->
    try {
      resolve(get())
    } catch (e: Exception) {
      reject(e.cause ?: e)
    }
  }
}

