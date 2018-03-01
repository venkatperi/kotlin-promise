package com.vperi.promise

import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.then
import nl.komponents.kovenant.unwrap

inline fun <reified T : Any?> wrap(x: T): Promise<T, Exception> {
  val defer = deferred<T, Exception>()
  when (x) {
    is Promise<*, *> -> {
      x.success { defer.resolve(it as T) }
        .fail { defer.reject(it as Exception) }
    }
    else -> defer.resolve(x)
  }
  return defer.promise
}

inline fun <TArg : Any?, reified TRes : Any?> wrap(fn: (TArg) -> TRes,
  arg: Any? = null): Promise<TRes, Exception> {
  return wrap(fn(arg as TArg))
}

inline fun <reified TRes : Any?> wrap(
  fn: () -> TRes): Promise<TRes, Exception> {
  return wrap(fn())
}

inline fun <T1Arg : Any?, reified T1Res : Any?, reified T2Arg : Any?,
  reified T2Res : Any?> pseq(first: (T1Arg) -> T1Res,
  crossinline second: (T2Arg) -> T2Res,
  arg: T1Arg?): Promise<T2Res, Exception> {

  val defer = deferred<T2Res, Exception>()

  wrap(first, arg).success {
    wrap(second, it)
      .success(defer::resolve)
      .fail(defer::reject)
  }.fail(defer::reject)

  return defer.promise
}

inline fun <reified T1Res : Any?,
  reified T2Arg : Any?,
  reified T2Res : Any?> pseq(
  first: () -> T1Res,
  crossinline second: (T2Arg?) -> T2Res): Promise<T2Res, Exception> {

  val defer = deferred<T2Res, Exception>()

  wrap(first).success {
    wrap(second, it)
      .success(defer::resolve)
      .fail(defer::reject)
  }.fail(defer::reject)

  return defer.promise
}

inline infix fun <reified V, R> Promise<V, Exception>.thenp(
  noinline next: (V) -> R): Promise<R, Exception> {
  return this
    .then(::wrap)
    .then {
      it.then(next)
    }.unwrap()
}
