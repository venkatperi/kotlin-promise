package com.vperi.promise

import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.all
import nl.komponents.kovenant.any
import nl.komponents.kovenant.deferred
import javax.swing.text.html.HTML.Tag.P

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value.
 *
 * @param executor A function that is passed with the arguments
 *    resolve and reject. The executor function is executed immediately,
 *    passing resolve and reject functions (the executor is called
 *    before the Promise constructor even returns the created object).
 *
 *    The resolve and reject functions, when called, resolve or reject
 *    the promise, respectively. The executor normally initiates some
 *    asynchronous work, and then, once that completes, either calls
 *    the resolve function to resolve the promise or else rejects it
 *    if an error occurred.
 *
 *    If an error is thrown in the executor function, the promise
 *    is rejected.
 */
class _P<out V>(executor: Executor<V>) {

  private val defer = deferred<V, Exception>()

  internal val promise by lazy { defer.promise }

  init {
    try {
      executor(defer::resolve, defer::reject)
    } catch (e: Exception) {
      defer.reject(e)
    }
  }

  companion object {
    internal fun <V> of(other: Promise<V, Exception>): P<V> {
      return P({ resolve, reject ->
        other.success(resolve).fail(reject)
      })
    }

    /**
     * Returns a Promise object that is resolved with the given value.
     */
    fun <V> resolve(value: V? = null): P<V?> {
      return P({ res, _ -> res(value) })
    }

    /**
     * Returns a [P] object that is rejected with the given reason.
     */
    fun reject(err: Exception): P<Unit> {
      return P({ _, rej -> rej(err) })
    }

    /**
     * Returns a single [P] that resolves when all of the promises
     * in the iterable argument have resolved or when the [Iterable]
     * argument contains no promises. It rejects with the reason of
     * the first promise that rejects.
     */
    fun <V> all(items: Iterable<P<V>>): P<List<V>> {
      return P.of(all(items.map { it.promise }))
    }

    /**
     * Returns a [P] that resolves or rejects as soon as one of the
     * promises in the iterable resolves or rejects, with the value or
     * reason from that promise.
     */
    fun <V> race(items: Iterable<P<V>>): P<V> {
      return P({ resolve, reject ->
        any(items.map { it.promise })
            .success { resolve(it) }
            .fail { reject(AggregateException(it)) }
      })
    }
  }
}

