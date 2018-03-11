package com.vperi.promise

typealias Executor<V> = P<V>.((V) -> Unit, (Throwable) -> Unit) -> Unit

typealias SuccessHandler<V, X> = (V) -> X

typealias FailureHandler<X> = (Throwable) -> X

