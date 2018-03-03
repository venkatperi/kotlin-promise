package com.vperi.vow

typealias Executor<V> = ((V) -> Unit, (Exception) -> Unit) -> Unit
