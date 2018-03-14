package com.vperi.promise.internal

import com.vperi.promise.P
import com.vperi.promise.promise

class Helper {
  companion object {
    fun success(sleep: Long): P<Unit> {
      return promise { r, _ ->
        Thread.sleep(sleep)
        r(Unit)
      }
    }

    fun fail(sleep: Long): P<Unit> {
      return promise { _, r ->
        Thread.sleep(sleep)
        r(Exception("fail"))
      }
    }

    fun failAt(total: Int, failAt: Int, sleep: (Int) -> Int): List<P<Unit>> {
      return (0 until total).map {
        when (it) {
          failAt -> fail(sleep(it).toLong() / 2)
          else -> success(sleep(it).toLong())
        }
      }
    }
  }
}