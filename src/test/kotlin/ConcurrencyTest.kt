
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.Callable

class ConcurrencyTest {

    @Test
    fun sanityCheck() {
        Observable.just("hello").test()
                .assertValue("hello")
                .assertComplete()
    }

    @Test
    fun definitelyNotConcurrent() {
        Observable.range(1, 5)
                .flatMap { Observable.just(it)
                        .printCurrentThread()
                }
                .test()
                .assertComplete()
    }

    private fun <T> Observable<T>.printCurrentThread(): Observable<T> = doOnNext {
        println("Current Thread: ${Thread.currentThread().name}")
    }

    @Test
    fun schedulersActuallyMakeNewThreads() {
        Observable.range(1, 5)
                .flatMap { Observable.just(it)
                        .subscribeOn(Schedulers.io())
                        .map { number ->
                            val name = Thread.currentThread().name
                            println("Value: $number, Thread Name: $name")
                            name
                        }
                }
                .collect(
                        Callable<MutableSet<String>>{ mutableSetOf() },
                        BiConsumer { set, threadName -> set.add(threadName) })
                .doOnSuccess { println("Size of set: ${it.size}") }
                .test()
    }
}