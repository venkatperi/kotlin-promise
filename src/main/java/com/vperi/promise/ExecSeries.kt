package com.vperi.promise

import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task

typealias AnyPromise = Promise<Any?, Exception>

@Suppress("unused")
class ExecSeries(
  private val tasks: Array<Function1<*, Any?>>,
  initial: Any?,
  private val options: ExecOptions = ExecOptions()) {

  val promise get() = defer.promise
  @Suppress("unused")
  var stop = false
  var errors: ArrayList<Exception> = ArrayList()

  private var current: AnyPromise = Promise.of(initial)
  private var idx = 0
  private val hasErrors: Boolean get() = errors.size > 0
  private val done: Boolean get() = idx >= tasks.size
  private val defer = deferred<Any?, Exception>()
  private var started: Boolean = false

  fun get(): Any? {
    run()
    return promise.get()
  }

  private fun nextOrDone() {
    if (hasErrors && options.stopOnError) {
      defer.reject(errors.last())
      return
    }

    val d = deferred<Any?, Exception>()
    current.success { prev ->
      if (done) {
        defer.resolve(prev)
        return@success
      }
      current = d.promise
      val t = { tasks[idx++](prev) }
      task(body = t)
        .success {
          d.resolve(it)
          nextOrDone()
        }
        .fail {
          errors.add(it)
          d.reject(it)
          nextOrDone()
        }
    }
      .fail {
        defer.reject(it)
      }
  }

  fun run() {
    if (started) return
    nextOrDone()
  }

  companion object {
    @Suppress("unused")
    private val TAG = ExecSeries::class.java.simpleName
  }
}