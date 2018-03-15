@file:Suppress("unused")

package com.vperi.benchmarks.custom

import com.vperi.promise.Promise
import com.vperi.promise.internal.TestHelper
import net.jodah.concurrentunit.Waiter
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class Custom() {

  var list: List<Promise<Unit>>? = null

  fun run(total: Int, failAt: Int, sleep: (Int) -> Int) {
    val waiter = Waiter()
    list = TestHelper.failAt(total, failAt, sleep)
    Promise.all(list!!)
      .catchX {
        waiter.resume()
      }

    waiter.await(100000)
  }

  //  @TearDown(Level.Invocation)
  fun teardown() {
    val waiter = Waiter()
    Promise.allDone(list!!).then {
      waiter.resume()
    }
    waiter.await(100000)
  }
}

const val numThreads = 100
const val sleepTime = 100

open class CustomFailAtStart : Custom() {
  @Benchmark
  fun benchmark() {
    run(numThreads, 1, { sleepTime })
  }
}

open class CustomFailMiddle : Custom() {
  @Benchmark
  fun benchmark() {
    run(numThreads, numThreads / 2, { sleepTime })
  }

}

open class CustomFailAtEnd : Custom() {
  @Benchmark
  fun benchmark() {
    run(numThreads, numThreads - 2, { sleepTime })
  }

}

