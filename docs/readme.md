[kotlin-promise](./index.md)

`kotlin-promise` is a minimal API for kotlin [promises](https://en.wikipedia.org/wiki/Futures_and_promises) based largely on native [Javascript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises) promises.

## Examples

#### Async Task

```
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

```
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

```
 promise<String>({ resolve, reject ->
      resolve("world")
    }).then {
      "hello $it"       // returns String
    }.then {
      it.length         // change type
    }.then(::println)   //=> 11
```

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

### Packages

| Name | Summary |
|---|---|
| [com.vperi.promise](com.vperi.promise/index.md) |  |

### Index

[All Types](alltypes/index.md)