package com.vperi.benchmarks.custom

import com.vperi.promise.internal.Helper
import com.vperi.promise.P
import net.jodah.concurrentunit.Waiter
import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
open class Custom() {

  var list: List<P<Unit>>? = null

  fun run(total: Int, failAt: Int, sleep: (Int) -> Int) {
    val waiter = Waiter()
    list = Helper.failAt(total, failAt, sleep)
    P.all(list!!)
      .catchX {
        //        P.allDone(list!!)
//      }.then {
        waiter.resume()
      }
    waiter.await(100000)
  }

  //  @TearDown(Level.Invocation)
  fun teardown() {
    val waiter = Waiter()
    P.allDone(list!!).then {
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

