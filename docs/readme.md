[kotlin-promise](./index.md)

[![](https://jitpack.io/v/com.vperi/kotlin-promise.svg)](https://jitpack.io/#com.vperi/kotlin-promise)

`kotlin-promise` is a minimal API for kotlin [promises](https://en.wikipedia.org/wiki/Futures_and_promises) based largely on native [Javascript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises) promises.

## Examples

#### Async Task

``` kotlin
 promise<String>({ resolve, reject ->
  // This block runs in a separate thread &
  // Eventually returns a string
  Thread.Sleep(500)
  resolve("world")
}).then { //it: String ->
  println("hello " + it)			//=> hello world
}
```

#### Handle Errors

``` kotlin
promise<String>({ resolve, reject ->
  // Runs in a separate thread &
  // eventually throws an exception
  Thread.sleep(500)
  throw Exception("some error")
}).then { //it: String ->
  println("hello " + it)			// doesn't get here
}.catch{ // it : Throwable ->
  println(it.message)         //=> some error
}
```

#### Chaining Promises

``` kotlin
promise<String>({ resolve, reject ->
  resolve("world")
}).then {
  "hello $it"       // returns String
}.then {
  it.length         // change type
}.then(::println)   //=> 11
```

## Creating promises

``` kotlin
fun promise<V>(executor: Executor<V>): P<V>
```

`promise()` returns a new promise which eventually resolves to a value of type `V` or an rejects with a `Throwable` reason.

### Parameters

* `executor` A function that accepts two arguments arguments,
`resolve` and `reject`. The executor function is immediately executed asynchronously, passing `resolve` and `reject` functions (the executor may be called before `promise()`  even returns the created object).

### Description

The `resolve` and `reject` functions, when called, resolve or reject the promise, respectively. The executor normally initiates some asynchronous work, and then, once that completes, either calls the `resolve` function to resolve the promise or else `reject` if if an error occurred.

If an error is thrown in the executor function, the promise
is rejected.

## Installation

Install with [jitpack](https://jitpack.io/#com.vperi/kotlin-promise/):

### Gradle

``` gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  compile 'com.vperi:kotlin-promise:<latest-version-here>'
}

```

## Converting Callbacks to Promises

### Java Futures

``` kotlin
fun <V> Future<V>.toPromise(): Promise<V> {
  return promise { resolve, reject ->
    try {
      resolve(get())
    } catch (e: Exception) {
      reject(e.cause ?: e)
    }
  }
}
```

Usage:

``` kotlin
val someFuture: Future<V>
someFuture.promise()
  .then { // it: V ->
    ...
  }
  .catch { // it: Throwable ->
    ...
  }
```

### Google Android GMS Tasks to Promises

``` kotlin
fun <T> Task<T>.promise(): P<T> =
  promise({ resolve, reject ->
    addOnCompleteListener {
      when {
        it.isSuccessful -> resolve(it.result)
        else -> reject(it.exception!!)
      }
    }
  })

// Special case: Task<Void> -> P<Unit>
@JvmName("promiseVoid")
fun Task<Void>.promise(): P<Unit> =
  promise({ resolve, reject ->
    addOnCompleteListener {
      when {
        it.isSuccessful -> resolve(Unit)
        else -> reject(it.exception!!)
      }
    }
  })
```

Usage:

``` kotlin
val someTask: Task<V>
someTask.promise()
 .then { // it: -> V
   ...
 }
 .catch { // it: Throwable ->
   ...
 }
```

### Packages

| Name | Summary |
|---|---|
| [com.vperi.promise](com.vperi.promise/index.md) |  |

### Index

[All Types](alltypes/index.md)