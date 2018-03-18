package com.vperi.promise

/**
 * Discriminated union that captures the settlement result of
 * a [Promise] (fulfillment or rejection)
 */
@Suppress("unused")
sealed class Result<out V> {

  /**
   * Value for fulfillment state
   */
  data class Value<out V>(val value: V) : Result<V>() {
    override fun toString(): String {
      return "Value[$value]"
    }
  }

  /**
   * Error for rejection state
   */
  class Error<out V>(val error: Throwable) : Result<V>() {
    override fun toString(): String {
      return "Error[${error.message}]"
    }
  }
}