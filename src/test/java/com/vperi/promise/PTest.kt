package com.vperi.promise

import com.vperi.promise.internal.all
import net.jodah.concurrentunit.Waiter
import org.junit.Before
import org.junit.Test

class PTest {
  private var waiter = Waiter()

  @Before
  fun beforeEach() {
    waiter = Waiter()
  }

  @Test
  fun already_resolved_promise() {
    P.resolve(1)
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
    P.reject(Exception("test msg"))
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
    promise<Int>({ _, _ ->
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
    waiter.await(1000, 2)
  }

  @Test
  fun special_case_nothing_to_unit() {
    promise<Int>({ resolve, _ -> resolve(1) })
      .then {
        throw Exception("test")
        Unit    //Can't return Nothing, so return void
      }
      .catch {
        waiter.assertEquals(it.message, "test")
      }
      .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun catch_propagates_result_when_not_rejected() {
    P.resolve(1)
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
    P.resolve(1)
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
    P.resolve(1)
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
  fun uncaught_exception() {
    val uncaught = { e: Throwable? ->
      waiter.assertEquals("uncaught exception", e?.message)
      waiter.resume()
    }
    P.onUnhandledException += uncaught
    P.reject(Exception("uncaught exception"))
      .then { waiter.fail() }
    waiter.await(1000)
    P.onUnhandledException -= uncaught
  }

  @Test
  fun finally_is_called_when_resolved() {
    P.resolve(1)
      .catch {
        waiter.fail()
        0
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
    P.reject(Exception("test"))
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
    P.resolve(1)
      .then {
        waiter.resume()
        P.resolve(it + 1)
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
    P.resolve(1)
      .delay(500)
      .then {
        waiter.assertEquals(1, it)
        waiter.resume()
      }
    waiter.await(1000)
  }

  @Test
  fun all1() {
    P.all(listOf(P.resolve(1), P.resolve(2), P.resolve(3)))
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
    P.all(listOf(
      P.resolve(1).delay(1500),
      P.resolve(2).delay(200),
      P.resolve(3).delay(600)))
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
  fun all3() {
    P.all(listOf(
      P.resolve(1).delay(400),
      promise { _, r ->
        Thread.sleep(600)
        r(Exception("test"))
      },
      P.resolve(3).delay(2500),
      P.resolve(3).delay(100)))
      .then {
        waiter.fail()
      }
      .catch {
        waiter.assertEquals("test", it.message)
        waiter.resume()
      }
    waiter.await(10000)
  }

}