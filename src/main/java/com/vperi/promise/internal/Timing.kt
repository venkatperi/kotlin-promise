package com.vperi.promise.internal

class Timing(autoStart: Boolean = true) {
  private val events = HashMap<String, Long>()

  val start: Long
    get() = events["start"] ?: 0

  init {
    if (autoStart)
      mark("start")
  }

  fun mark(name: String) {
    events[name] = System.nanoTime()
  }

  operator fun get(name: String): Long {
    return (events[name] ?: 0) - start
  }

  fun msTime(name: String): Double {
    return this[name] / 1e6
  }
}

