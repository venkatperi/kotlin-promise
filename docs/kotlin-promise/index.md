[kotlin-promise](./index.md)

`kotlin-promise` is a minimal API for kotlin [promises](https://en.wikipedia.org/wiki/Futures_and_promises) based largely on native [Javascript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises) promises.

## Creating promises

```
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

### Examples

```
 promise<String>({ resolve, reject ->
      //some async operation which eventually gets a string
      Thread.Sleep(500)
      resolve("world")
    }).then { //it: String ->
      println("hello " + it)			//=> hello world
    }
```

## Promise API

### Promise.then

The `then()` method returns a promise. It takes up to two arguments: handlers for the success and failure cases of the promise.

Given a promise:

```
val p : P<V> = promise<V>({resolve, reject -> ... })
```

Handle only fulfillment (used with chaining):

```
p.then(onFulfilled: (V) -> X): P<X>
```

Handle fulfillment &amp; recover from rejection:

```
p.then(onFulfilled: (V) -> X, onRejected: (Exception) -> X): P<X>
```

Or just receive notification when promise settles:

```
p.then(): P<Unit>
```

#### Parameters

* `onFulfilled` A function called if the promise is fulfilled. This function has one argument, the fulfillment value of type `V`.

* `onRejected` An optional function called if the promise is rejected. This function has one argument, the rejection reason (`Throwable`).

#### Returns

A promise in the pending status. The handler function (`onFulfilled` or `onRejected`) gets called asynchronously. After the invocation of the handler function, if the handler function:

* returns a value, the promise returned by `then` gets resolved with the returned value as its value;
* throws an error, the promise returned by `then` gets rejected with the thrown error as its value;

Calling `then()` without handlers returns a promise which resolves to `Unit` when the original promise resolves; or rejects with the reason of rejection if the original promise is rejected.

Calling `then` with a single paramter (handler for success) is the most often use case for chaining.

#### Examples

```
promise<String>({ resolve, _ ->
      resolve("world")
    }).then {  //it: String ->
      val str = "hello $it"
      println(str)              //=> hello world
      str.length
    }.then {  //it: Int ->
      println(it)               //=> 11
    }
```

### catch

The `catch` method returns a promise and deals with rejected cases only. It behaves the same as calling `then({it}, onRejected)` (which is how it is implemented internally).

#### Parameters

* `onRejected` A function called when the promise is rejected. This function has one argument:
  * `reason` The rejection reason.
  The promise returned by catch() is rejected if onRejected throws an error or returns a promise which is itself rejected; otherwise, it is resolved.

## Chaining

The then method returns a promise which allows for method chaining.

If the function passed as handler to then returns a promise, an equivalent promise will be exposed to the subsequent then in the method chain.

### Packages

| Name | Summary |
|---|---|
| [com.vperi.promise](com.vperi.promise/index.md) |  |

### Index

[All Types](alltypes/index.md)