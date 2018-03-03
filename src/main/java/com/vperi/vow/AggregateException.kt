package com.vperi.vow

class AggregateException(val items: Iterable<Exception>) : Exception()