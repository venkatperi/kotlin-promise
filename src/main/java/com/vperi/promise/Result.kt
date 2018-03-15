package com.vperi.promise

sealed class Result<out V> {
  data class Value<out V>(val value: V) : Result<V>() {
    override fun toString(): String {
      return "Value[$value]"
    }
  }

  class Error<out V>(val error: Throwable) : Result<V>() {
    override fun toString(): String {
      return "Error[${error.message}]"
    }
  }
}