
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.TimeUnit

class ConcurrencyTest {

    companion object {
        val MAIN = "main".toRegex()

        val IO_THREAD = "RxCachedThreadScheduler-*".toRegex()
    }

    private fun <T> Observable<T>.debug(name: String = ""): Observable<T> = doOnNext { print(name, it) }

    private fun <T> Observable<T>.printObservableEvents(): Observable<T> =
            doOnSubscribe { print("Event:", "onSubscribed") }
            .doOnComplete { print("Event:", "onCompleted") }
            .doOnError { print("Event:", "onError") }
            .doOnDispose { print("Event:", "onDispose") }

    private fun <T> print(name: String, value: T) {
        println("[${Thread.currentThread().name}] $name $value")
    }

    private fun <T> Observable<T>.assertThread(expected: Regex): Observable<T> = doOnNext {
        Assert.assertTrue(Thread.currentThread().name.contains(expected))
    }

    @Test
    fun sanityCheck() {
        Observable.just("hello")
                .debug()
                .assertThread(MAIN)
                .test()
                .assertValue("hello")
                .assertComplete()
    }

    /**
     * This is also a sanity check, but it's more of a precursor to the next test.
     */
    @Test
    fun nothingSpecifiedSubscribesOnMainThread() {
        Observable.range(1, 5)
                .flatMap { Observable.just(it).debug() }
                .assertThread(MAIN)
                .test()
                .assertComplete()
    }

    /**
     * Schedulers.trampoline() specifies the main scheduler.
     *
     * Still nothing crazy here. Sanity check pt 3.
     */
    @Test
    fun subscribingOnMainWithTrampoline() {
        Observable.range(1, 5)
                .flatMap { Observable.just(it)
                        .assertThread(MAIN)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .assertThread(MAIN)
                .test()
    }

    @Test
    fun subscribingOnTheIOThread() {
        Observable.range(1, 5)
                .flatMap { Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .debug("Just subscribed on IO Schedulers")
                        .assertThread(IO_THREAD)
                        .observeOn(Schedulers.trampoline())
                        .debug("Observed on Trampoline")
                        .assertThread(MAIN)
                }
                .subscribeOn(Schedulers.trampoline())
                .assertThread(MAIN)
                .test()
                .assertComplete()
    }

    /**
     * Processing data concurrently is actually very straightforward:
     *
     * 1. Create a new Observable with the many ways to create an observable. In this example, we use
     *    Observable.just(Int) to create a new Observable from the Int value emitted by the range operator.
     *
     * 2. subscribeOn a thread instance by invoking the Schedulers.io() method included with the Rx library.
     *    Calling Schedulers.io() will create a new thread instance from an unbounded thread pool. Subsequent calls
     *    to this method will create more and more threads as needed by the stream--this may be dangerous for very
     *    strenuous streams.
     *
     * This creates a stream that runs its operators in parallel. But because the subscriber (in this case, the test
     * subscriber with the test() method) is on a different thread from the operators that were subscribed to on threads
     * from the Schedulers.io() pool, it will never receive these events, therefore we will not receive any termination-
     * related callbacks.
     *
     * We now have an observable that does not receive events, but we reach the end of the code block, so therefore we
     * do not see the end of the emitted stream.
     *
     * A note on the .delay() operator:
     * This delay is here to simulate a process that takes a while to emit.
     */
    @Test
    @Ignore("This test is somewhat unpredictable right now.")
    fun processingInParallelWithoutWaitingForTheSubscriber() {
        Observable.range(1, 5)
                .flatMap { Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .assertThread(IO_THREAD)
                        .debug("New Thread")
                        .delay(10, TimeUnit.SECONDS)
                        .debug("Delayed (This should not be printed)")
                }
                .printObservableEvents()
                .assertThread(MAIN)
                .test()
                .assertNotComplete()
                .assertNoErrors()
                .assertNotTerminated()
    }
}