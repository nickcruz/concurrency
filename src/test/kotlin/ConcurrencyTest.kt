
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Test

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
}