package com.vperi.promise

sealed class Result<out V> {
  data class Value<out V>(val value: V) : Result<V>()
  class Error<out V>(val error: Exception) : Result<V>()
}