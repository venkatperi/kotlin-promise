package com.vperi.promise

import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.lang.Thread.yield
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture

class MiscTest : BaseTest() {

  @Test(expected = AssertionError::class)
  fun cannot_cancel_completable_future() {
    val f = CompletableFuture.runAsync {
      Thread.sleep(2000)
      waiter.fail("future wasn't cancelled")  //This is executed, even though we cancel the future
    }.whenComplete { _, _ ->
      waiter.resume()  // doesn't get here, otherwise test would complete OK
    }
    Thread.sleep(250)
    f.cancel(true)                            // no effect
    waiter.await(5000)
  }

  @Test
  fun can_cancel_settable_future() {
    expect(1)
    val f = SettableFuture.create<Int>()

    f.addListener(Runnable {
      expect(5)
      waiter.resume()
    }, commonPool)

    commonPool.submit {
      expect(3)
      Thread.sleep(2000)
      expectUnreached()
    }

    expect(2)
    Thread.sleep(250)
    expect(4)
    f.cancel(true)
    waiter.await(5000)
    finish(6)
  }

  @Test
  fun testCancellableAwaitFuture() = runBlocking {
    expect(1)
    val toAwait = CompletableFuture<String>()
    val job = launch(coroutineContext, CoroutineStart.UNDISPATCHED) {
      expect(2)
      try {
        toAwait.await() // suspends
      } catch (e: CancellationException) {
        expectUnreached() // CompletableFuture doesn't cancel as expected0
//        expect(5) // should throw cancellation exception
        throw e
      }
    }
    expect(3)
    job.cancel() // cancel the job
    toAwait.complete("fail") // too late, the waiting job was already cancelled
    expect(4) // job processing of cancellation was scheduled, not executed yet
    yield() // yield main thread to job
    finish(5)
  }

}
