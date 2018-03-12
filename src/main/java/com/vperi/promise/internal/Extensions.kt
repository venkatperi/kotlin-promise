package com.vperi.promise.internal

import com.vperi.promise.P
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

fun <T, X> CompletableFuture<T>.then(
  success: (T) -> X,
  failure: ((Throwable) -> X)?): CompletableFuture<X> {
  return CompletableFuture<X>().apply {
    this@then.handle { value, error: Throwable? ->
      when (error) {
        null -> complete(success(value))
        else -> {
          if (failure != null)
            complete(failure(error))
          else
            completeExceptionally(error)
        }
      }
    }
  }
}

fun <T, X> CompletableFuture<T>.then(success: (T) -> X):
  CompletableFuture<X> =
  then(success, null)

fun <T> CompletableFuture<T>.catch(
  failure: ((Throwable) -> T)): CompletableFuture<T> =
  then({ it }, failure)

@JvmName("catchUnit")
fun CompletableFuture<*>.catch(
  failure: ((Throwable) -> Unit)): CompletableFuture<Unit> =
  then({ }, failure)

inline fun <reified T> List<CompletableFuture<out T>>.allAsList():
  CompletableFuture<List<T>> {
  val results = HashMap<Int, T>()
  val future = CompletableFuture<List<T>>()
  val done = AtomicInteger(0)

  this.forEachIndexed { index, item ->
    item.then({
      results[index] = it
      if (done.incrementAndGet() >= size) {
        future.complete((0 until size).map { results[it]!! })
      }
    }, {
      future.completeExceptionally(it)
    })
  }

  future.catch {
    forEach { it.cancel(true) }
  }

  return future
}

inline fun <reified V> P.Companion.all(promises: List<P<V>>): P<List<V>> {
  val futures = promises.map { (it as PImpl<V>).resultFuture }
  return PImpl(futures.allAsList())
}