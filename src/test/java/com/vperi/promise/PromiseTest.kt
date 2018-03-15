package com.vperi.promise

import com.vperi.promise.internal.TestHelper
import net.jodah.concurrentunit.Waiter
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

fun <T> rejectWithDelay(error: Throwable, delay: Long): Promise<T> {
  return promise { _, r ->
    Thread.sleep(delay)
    r(error)
  }
}

class PromiseTest {
  private var waiter = Waiter()

  @Before
  fun beforeEach() {
    waiter = Waiter()
  }

  @Test
  fun already_resolved_promise() {
    Promise.resolve(1)
      .then {
        waiter.resume()
        waiter.assertEquals(1, it)
      }
      .then {
        waiter.resume()
      }
      .catch {
        waiter.fail("Shouldn't get here")
      }
      .then {
        waiter.resume()
      }
    waiter.await(1000, 2)
  }

  @Test
  fun already_rejected_promise() {
    Promise.reject(Exception("test msg"))
      .catch {
        waiter.assertEquals("test msg", it.message)
        waiter.resume() // should get here
      }
      .then {
        waiter.resume()  //and here
      }
    waiter.await(1000, 2)
  }

  @Test
  fun executor_is_invoked_asynchronously() {
    var x = 0
    promise<Int>({ resolve, _ ->
      Thread.sleep(500)
      x = 2
      resolve(1)
    }).then { waiter.assertTrue(it == 1) }
      .then { waiter.resume() }
      .catch { waiter.fail() }
    waiter.assertEquals(0, x)
    waiter.await(1000)
    waiter.assertEquals(2, x)
  }

  @Test
  fun reject() {
    promise<Int>({ _, reject ->
      Thread.sleep(500)
      reject(Exception("test"))
    }).then { waiter.fail() }
      .catch {
        waiter.assertEquals(it.message, "test")
        waiter.resume()   //should get here
      }
      .then {
        waiter.resume()   //should get here
      }
    waiter.await(1000, 2)
  }

  @Test
  fun executor_throws_exception() {
    promise<Unit>({ _, _ ->
      Thread.sleep(500)
      throw Exception("test")
    }).then { waiter.fail() }
      .catch {
        waiter.assertEquals("test", it.message)
        waiter.resume()   //should get here
      }
      .then {
        waiter.resume()   //should get here
      }
    waiter.await(2000, 2)
  }

  @Test
  fun cancel_pending_promise() {
    val p = promise<Int>({ _, _ ->
      Thread.sleep(1500)
    })
    p.then {
      waiter.fail()
    }
      .catch {
        waiter.resume()   //should get here
      }
      .then {
        waiter.resume()   //should get here
      }

    Thread.sleep(100)
    p.cancel()

    waiter.await(2000, 2)
  }

  @Test
  fun special_case_nothing_to_unit() {
    promise<Int>({ resolve, _ -> resolve(1) })
      .then {
        throw Exception("test")
        @Suppress("UNREACHABLE_CODE")
        Unit    //Can't return Nothing, so return void
      }
      .catch {
        waiter.assertEquals(it.message, "test")
      }
      .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun multiple_handlers_will_get_called() {
    val p = Promise.resolve(1)
      .delay(500)

    (0..9).forEach {
      p.then {
        waiter.resume()
      }
    }
    waiter.await(10000, 10)
  }

  @Test
  fun catch_propagates_result_when_not_rejected() {
    Promise.resolve(1)
      .catch {
        waiter.fail()
        0
      }
      .then { waiter.assertEquals(it, 1) }
      .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun fix_error_with_catch_and_continue_chain() {
    Promise.resolve(1)
      .then {
        waiter.resume()
        when {
          it != 2 -> throw Exception("was expecting 2")
          else -> it
        }
      }
      .catch {
        waiter.resume()
        waiter.assertEquals(it.message, "was expecting 2")
        2
      }
      .then {
        waiter.resume()
        waiter.assertEquals(2, it)
      }
      .then { waiter.resume() }
    waiter.await(1000, 4)
  }

  @Test
  fun catch_down_the_line() {
    Promise.resolve(1)
      .then { throw Exception("test") }
      .then { waiter.fail() }
      .then { waiter.fail() }
      .then { waiter.fail() }
      .catch {
        waiter.resume()
        waiter.assertEquals(it.message, "test")
      }
      .then { waiter.resume() }
    waiter.await(1000, 2)
  }

  @Test
  fun catchX_sends_null_on_success() {
    Promise.onUnhandledException += {
      println(it!!.message)
    }
    Promise.resolve("test")
      .catchX { 1 }
      .then {
        waiter.assertNull(it)
        waiter.resume()
      }
    waiter.await(1000)
  }

  @Test
  fun catchX_on_failure() {
    Promise.reject(Exception())
      .catchX { 1 }
      .then {
        waiter.assertEquals(1, it)
        waiter.resume()
      }
    waiter.await(1000)
  }

  @Test
  fun uncaught_exception() {
    val uncaught = { e: Throwable? ->
      waiter.assertEquals("uncaught exception", e?.message)
      waiter.resume()
    }
    Promise.onUnhandledException += uncaught
    Promise.reject(Exception("uncaught exception"))
      .then { waiter.fail() }
    waiter.await(1000)
    Promise.onUnhandledException -= uncaught
  }

  @Test
  fun finally_is_called_when_resolved() {
    Promise.resolve(1)
      .catch {
        waiter.fail()
        2
      }
      .finally {
        when (it) {
          is Result.Error -> waiter.fail()
          is Result.Value -> waiter.assertEquals(1, it.value)
        }
        waiter.resume()
      }
    waiter.await(1000, 1)
  }

  @Test
  fun finally_is_called_when_rejected() {
    Promise.reject(Exception("test"))
      .then { waiter.resume() }
      .then { waiter.resume() }
      .finally {
        when (it) {
          is Result.Value -> waiter.fail()
          is Result.Error -> waiter.assertEquals("test", it.error.message)
        }
        waiter.resume()
      }
    waiter.await(1000, 1)
  }

  @Test
  fun unwrap_nested_promise() {
    Promise.resolve(1)
      .then {
        waiter.resume()
        Promise.resolve(it + 1)
      }
      .then { it: Int ->
        waiter.resume()
        waiter.assertEquals(2, it)
      }
      .then { waiter.resume() }
      .catch { waiter.fail() }
    waiter.await(1000, 3)
  }

  @Test
  fun example_1() {
    promise<String>({ resolve, _ ->
      resolve("world")
    }).then {
      val str = "hello $it"
      println(str)
      str.length
    }.then {
      println(it)   //=>
      waiter.resume()
    }
    waiter.await(1000)
  }

  @Test
  fun delay() {
    Promise.resolve(1)
      .delay(500)
      .then {
        waiter.assertEquals(1, it)
        waiter.resume()
      }
    waiter.await(1000)
  }

  @Test
  fun all1() {
    Promise.all(listOf(Promise.resolve(1), Promise.resolve(2), Promise.resolve(3)))
      .then {
        it.reduce { acc, x -> acc + x }
      }
      .then {
        waiter.assertEquals(6, it)
        waiter.resume()
      }
      .catch(waiter::fail)
    waiter.await(100000)
  }

  @Test
  fun all2() {
    Promise.all(listOf(
      Promise.resolve(1).delay(1500),
      Promise.resolve(2).delay(200),
      Promise.resolve(3).delay(600)))
      .then {
        it.reduce { acc, x -> acc + x }
      }
      .then {
        waiter.assertEquals(6, it)
        waiter.resume()
      }
      .catch(waiter::fail)
    waiter.await(5000)
  }

  @Test
  fun all_rejects_immediately_on_first_rejection() {
    val list = listOf(
      Promise.resolve(1).delay(1400),
      rejectWithDelay(Exception("test"), 600),
      Promise.resolve(3).delay(2500),
      Promise.resolve(3).delay(100))
    Promise.all(list)
      .then {
        waiter.fail()
      }
      .catch {
        waiter.assertEquals("test", it.message)
        waiter.resume()
      }
    waiter.await(10000)
  }

  @Test
  fun allDone_waits_for_all_to_settle() {
    val list = listOf(
      Promise.resolve(1).delay(1400),
      rejectWithDelay(Exception("test"), 600),
      Promise.resolve(3).delay(2500),
      Promise.resolve(3).delay(100))
    Promise.allDone(list)
      .then {
        waiter.assertEquals(list.size, it.size)
        waiter.assertTrue(it[1] is Result.Error)
        waiter.resume()
      }
    waiter.await(10000)
  }

  @Test
  fun race_resolves_with_first_resolution() {
    Promise.race(listOf(
      Promise.resolve(1).delay(1500),
      Promise.resolve(2).delay(200),
      Promise.resolve(3).delay(600)))
      .then {
        waiter.assertEquals(2, it)
        waiter.resume()
      }
      .catch(waiter::fail)
    waiter.await(5000)
  }

  @Test
  fun race_rejects_with_first_rejection() {
    Promise.race(listOf(
      Promise.resolve(1).delay(1500),
      rejectWithDelay(Exception("test"), 300),
      Promise.resolve(3).delay(600)))
      .then {
        waiter.fail()
      }
      .catch {
        waiter.assertEquals("test", it.message)
        waiter.resume()
      }

    waiter.await(5000)
  }

  @Test
  fun lots_of_promises() {
    val waiter = Waiter()
    val list = TestHelper.failAt(1000, 50, { 1000 })
    Promise.allDone(list)
      .then {
        Promise.allDone(list)
      }
      .then {
        waiter.resume()
      }
    waiter.await(100000)
  }

  @Test
  fun future_to_promise() {
    Executors.newCachedThreadPool().submit {
      Thread.sleep(500)
    }.toPromise().then {
      waiter.resume()
    }
    waiter.await(1000)
  }

  @Test
  fun future_to_promise_failure() {
    Executors.newCachedThreadPool().submit {
      Thread.sleep(500)
      throw Exception("test")
    }.toPromise()
      .then {
        waiter.fail()
      }
      .catch {
        waiter.assertEquals("test", it.message)
        waiter.resume()
      }
    waiter.await(1000)
  }

}