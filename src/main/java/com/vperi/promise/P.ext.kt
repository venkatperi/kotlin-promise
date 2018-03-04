package com.vperi.promise

/**
 * Returns a [P] promise
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
fun <V> promise(executor: Executor<V>): P<V> {
  return PImpl(executor)
}

fun <V> P.Companion.resolve(value: V): P<V> {
  return promise({ r, _ -> r(value) })
}

fun P.Companion.reject(exception: Exception): P<Unit> {
  return promise({ _, r -> r(exception) })
}

fun <V> P<V>.catch(onRejected: FailureHandler<*>): P<V> =
  PImpl({ resolve, reject ->
    this.finally {
      when (it) {
        is Result.Value -> resolve(it.value)
        is Result.Error -> {
          try {
            resolve(onRejected(it.error) as V)
          } catch (e: Exception) {
            reject(e)
          }
        }
      }
    }
  })

@JvmName("nothingCatch")
fun <X> P<Nothing>.catch(onRejected: FailureHandler<X>): P<X> =
  promise({ resolve, reject ->
    this.finally {
      when (it) {
      // is Result.Value -> // shouldnt get here
        is Result.Error -> {
          try {
            resolve(onRejected(it.error))
          } catch (e: Exception) {
            reject(e)
          }
        }
      }
    }
  })

@JvmName("pThen")
fun <V, X> P<P<V>>.then(onResolved: SuccessHandler<V, X>): P<X> =
  promise({ resolve, reject ->
    this.then {
      it.then {
        try {
          resolve(onResolved(it))
        } catch (e: Exception) {
          reject(e)
        }
      }.catch(reject)
    }.catch {
      it
    }
  })
