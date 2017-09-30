# Testing Concurrency in RxJava
This repository contains a basic concurrency test in RxJava. There are several
ways to test concurrency behavior, but this test asserts time.

The main tool used is an RxJava `TestScheduler`. This helps us manipulate time
over each scheduler and predictably assert our outputs.

For more information, check out the article below:

#### [Companion Article on Medium](https://proandroiddev.com/testing-concurrency-in-rxjava-831804a9e526)