package com.vperi.vow

import net.jodah.concurrentunit.Waiter
import org.junit.Before
import org.junit.Test

class VowTest {

  var waiter = Waiter()

  @Before
  fun beforeEach() {
    waiter = Waiter()
  }

  @Test
  fun basic_resolve() {
    Vow<Int>({ resolve, _ -> resolve(1) })
        .then { waiter.assertTrue(it == 1) }
        .then { waiter.resume() }
        .catch { waiter.fail() }
    waiter.await(1000)
  }

  @Test
  fun basic_reject() {
    Vow<Int>({ _, reject -> reject(Exception("test")) })
        .then { waiter.fail() }
        .catch { waiter.assertEquals(it.message, "test") }
        .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun throw_exception() {
    Vow<Int>({ _, _ -> throw Exception("test") })
        .then { waiter.fail() }
        .catch { waiter.assertEquals(it.message, "test") }
        .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun throw_exception2() {
    Vow<Int>({ resolve, _ -> resolve(1) })
        .then { throw Exception("test") }
        .catch { waiter.assertEquals(it.message, "test") }
        .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun catch_down_the_line() {
    Vow<Int>({ resolve, _ -> resolve(1) })
        .then { throw Exception("test") }
        .then { waiter.fail() }
        .then { waiter.fail() }
        .then { waiter.fail() }
        .catch { waiter.assertEquals(it.message, "test") }
        .then { waiter.resume() }
    waiter.await(1000)
  }

  @Test
  fun unwrap_promises() {
    Vow<Int>({ resolve, _ -> resolve(1) })
        .thenP { Vow.resolve(it) }
        .then { waiter.assertTrue(it == 1) }
        .then { waiter.resume() }
        .catch { waiter.fail() }
    waiter.await(1000)
  }

  @Test
  fun resolve_promise() {
    Vow.resolve(1)
        .then { waiter.assertTrue(it == 1) }
        .then { waiter.resume() }
        .catch { waiter.fail() }
    waiter.await(1000)
  }

  @Test
  fun rejected_promise() {
    Vow.reject(Exception("test"))
        .catch { waiter.assertEquals(it.message, "test") }
        .then { waiter.resume() }
        .catch { waiter.fail() }
    waiter.await(1000)
  }

  @Test
  fun all() {
    val list = listOf(1, 2)
    Vow.all(list.map { Vow.resolve(it) })
        .then { waiter.assertEquals(it, list) }
        .then { waiter.resume() }
        .catch { waiter.fail() }
    waiter.await(1000)
  }

}