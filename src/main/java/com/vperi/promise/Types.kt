package com.vperi.promise

typealias Executor<V> = ((V) -> Unit, (Exception) -> Unit) -> Unit

typealias SuccessHandler<V, X> = (V) -> X

typealias FailureHandler<X> = (Exception) -> X

