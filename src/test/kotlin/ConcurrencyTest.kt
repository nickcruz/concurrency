
import io.reactivex.Observable
import org.junit.Test

class ConcurrencyTest {

    @Test
    fun firstTest() {
        Observable.just("hello").test()
                .assertComplete()
                .assertValue("hello")
    }
}