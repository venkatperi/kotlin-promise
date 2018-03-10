# kotlin-promie
A Promise is an object representing the eventual completion or failure of an asynchronous operation. 

`kotlin-promise` is  minimal API for kotlin [Promises](https://en.wikipedia.org/wiki/Futures_and_promises) and is based largely on native [Javascript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises) promises. 

## Creating Promises

Creates a promise instance.

#### Syntax
 `promise<V>(executor: ((V) -> Unit, (Exception) -> Unit) -> Unit)`

### Parameters
* `executor` A function that is passed with the arguments
`resolve` and `reject`. The executor function is executed asynchronously,
passing resolve and reject functions (the executor may be called
before the Promise constructor even returns the created object).

### Description
The `resolve` and `reject` functions, when called, resolve or reject
the promise, respectively. The executor normally initiates some
asynchronous work, and then, once that completes, either calls
the `resolve` function to resolve the promise or else `reject` if
if an error occurred.

If an error is thrown in the executor function, the promise
is rejected.

### Examples

````
 val p : P<String> = promise<String>({ resolve, reject ->
      //some async operation which eventually gets a string
      Thread.Sleep(500)
      resolve("world")
    }).then { //it: String -> 
      println("hello " + it)			//=> hello world
    }
````

## API
### then

#### Syntax
````
val p : P<X> = promise<X>(...)

p.then()  //returns P<Unit>

p.then({ value: X -> 
  // fulfillment
  })

p.then({ value: X -> 
    // fulfillment 
  }, { e: Exception -> 
    // rejection
  })
````

The `then` method returns a Promise. It takes up to two arguments: handlers for the success and failure cases of the Promise.

#### Parameters
* `onFulfilled`: A function called if the Promise is fulfilled. This function has one argument, the fulfillment value.

* `onRejected`: An optional function called if the Promise is rejected. This function has one argument, the rejection reason.

#### Returns
A Promise in the pending status. The handler function (onFulfilled or onRejected) gets called asynchronously. After the invocation of the handler function, if the handler function:

* returns a value, the promise returned by `then` gets resolved with the returned value as its value;
* throws an error, the promise returned by `then` gets rejected with the thrown error as its value;

Calling `then()` without handlers returns a promise which resolves to `Unit` then the original promise resolves or rejects with the reason of rejection if the original promise is rejected.

Calling `then` with a single paramter (handler for success) is the most often use case for chaining.

### catch

The `catch` method returns a Promise and deals with rejected cases only. It behaves the same as calling `then({it}, onRejected)` (which is how it is implemented internally).

#### Parameters
* `onRejected` A function called when the promise is rejected. This function has one argument:

  - `reason` The rejection reason. 
  
  The Promise returned by catch() is rejected if onRejected throws an error or returns a Promise which is itself rejected; otherwise, it is resolved.


## Chaining
The then method returns a Promise which allows for method chaining.

If the function passed as handler to then returns a Promise, an equivalent Promise will be exposed to the subsequent then in the method chain. 