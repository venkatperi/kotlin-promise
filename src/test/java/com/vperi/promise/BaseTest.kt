@file:Suppress("MemberVisibilityCanBePrivate")

package com.vperi.promise

import net.jodah.concurrentunit.Waiter
import org.junit.Before
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

open class BaseTest {
  var waiter = Waiter()
  var commonPool = ForkJoinPool.commonPool()

  @Before
  fun beforeEach() {
    waiter = Waiter()
  }

  /**
   * Is `true` when nightly stress test is done.
   */
  val isStressTest = System.getProperty("stressTest") != null

  val stressTestMultiplierSqrt = if (isStressTest) 5 else 1

  /**
   * Multiply various constants in stress tests by this factor, so that they run longer during nightly stress test.
   */
  val stressTestMultiplier = stressTestMultiplierSqrt * stressTestMultiplierSqrt

  private var actionIndex = AtomicInteger()
  private var finished = AtomicBoolean()
  private var error = AtomicReference<Throwable>()

  /**
   * Throws [IllegalStateException] like `error` in stdlib, but also ensures that the test will not
   * complete successfully even if this exception is consumed somewhere in the test.
   */
  fun error(message: Any, cause: Throwable? = null): Nothing {
    val exception = IllegalStateException(message.toString(), cause)
    error.compareAndSet(null, exception)
    throw exception
  }

  /**
   * Throws [IllegalStateException] when `value` is false like `check` in stdlib, but also ensures that the
   * test will not complete successfully even if this exception is consumed somewhere in the test.
   */
  inline fun check(value: Boolean, lazyMessage: () -> Any): Unit {
    if (!value) error(lazyMessage())
  }

  /**
   * Asserts that this invocation is `index`-th in the execution sequence (counting from one).
   */
  fun expect(index: Int) {
    val wasIndex = actionIndex.incrementAndGet()
    check(index == wasIndex) { "Expecting action index $index but it is only $wasIndex" }
  }

  /**
   * Asserts that this line is never executed.
   */
  fun expectUnreached() {
    error("Should not be reached")
  }

  /**
   * Asserts that this it the last action in the test. It must be invoked by any test that used [expect].
   */
  fun finish(index: Int) {
    expect(index)
    check(!finished.getAndSet(true)) { "Should call 'finish(...)' at most once" }
  }
}