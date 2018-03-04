package com.vperi.promise

class AggregateException(val items: Iterable<Exception>) : Exception()