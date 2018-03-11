package com.vperi.promise.internal

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class Dispatcher(
  private val executor: ExecutorService = Executors.newCachedThreadPool()) {

  fun <T> submit(job: () -> T): Future<T> {
    return executor.submit(job)
  }
}