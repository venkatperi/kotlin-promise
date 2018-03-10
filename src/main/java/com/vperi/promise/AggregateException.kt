package com.vperi.promise

data class AggregateException(val items: Iterable<Exception>) : Exception()