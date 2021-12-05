# one

An tool to help developer to use [Retrofit](https://github.com/square/retrofit)
elegantly while using [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines).

## Feature
1. Transform different data structs to one.
	- `{errorCode, data, messsage}`, `{status, data, messsage}` etc to one like `{code, data, message}`.
2. No try catch needed when call a suspend method.
	- try catch has been processed by reproxy interface.

## Example

```kotlin
// call `proxyRetrofit` after `Retrofit#create`, in this method it will handle
// exception automatically.
val service = retrofit.create(WanAndroidService::class.java).proxyRetrofit()
// test
service.test()
	.onSuccess { println("execute test success ==> $it") }
	.onFailure { println("execute test() failure ==> $it") }
	// userInfo
	.onFailureThen { service.userInfo() }
	?.onSuccess { println("execute userInfo success ==> $it") }
	?.onFailure { println("execute userInfo() failure ==> $it") }
	// banner
	?.onFailureThen { service.banner() }
	?.onSuccess { println("execute banner() success ==> $it") }
	?.onFailure { println("execute banner() failure ==> $it") }
```

Is that realy simple and clean without try catch? If so, continuate to read following
sections.

## Usage
1. Define a `BaseAPIResult`.
2. Define a `ThrowableResolver` to handle throwable and return a `BaseAPIRsult`
which contains failure.
3. Define a `ResponseThransformer` to transfrom different response data struct
to the same response data struct.
4. Add `TansformConverterFactory` to `Retrofit` Converter.Factory list at first.
5. Call `proxyRetrofit` after calling `Retrofit#create`.

Read [OneTest](https://github.com/ParadiseHell/one/blob/main/src/test/kotlin/org/paradisehell/one/OneTest.kt)
to understand details.

## About the one
Why the repository is named **one**, just because I cannot figure how to name it.

License
=======

    Copyright 2021 ParadiseHell.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
