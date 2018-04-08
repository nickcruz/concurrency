package concurrency

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * Created by Nick Cruz on 9/30/17
 */
class DownloaderTest {

    @Test
    fun concurrentDownload_Takes5Seconds() {
        val totalTime = measureTimeMillis {
            val actualList = downloadImages(DelayedRepository()).blockingGet()

            assertTrue(actualList.containsAll(setOf(
                    Image(1),
                    Image(2),
                    Image(3),
                    Image(4),
                    Image(5)
            )))
        }
        assertTrue(totalTime < 6000)
    }

    @Test
    fun sequentialDownload_Takes25Seconds() {
        val totalTime = measureTimeMillis {
            val actualList = downloadImagesSequentially(DelayedRepository()).blockingGet()

            assertTrue(actualList.containsAll(setOf(
                    Image(1),
                    Image(2),
                    Image(3),
                    Image(4),
                    Image(5)
            )))
        }
        assertTrue(totalTime < 26000)
    }

    @Test
    fun concurrentDownload_IsInstant() {
        val testScheduler = TestScheduler()

        val actualList = mutableListOf<Image>()

        downloadImages(DelayedRepository(testScheduler), testScheduler)
                .subscribe { list -> actualList.addAll(list)}

        // After 4 seconds, we can assert that our list is still empty.
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        assertTrue(actualList.isEmpty())

        // After a 5th second, we can assert that our list is loaded.
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        assertTrue(actualList.containsAll(setOf(
                Image(1),
                Image(2),
                Image(3),
                Image(4),
                Image(5)
        )))
    }

    @Test
    fun sequentialDownload_IsInstant() {
        val testScheduler = TestScheduler()

        val actualList = mutableListOf<Image>()

        downloadImagesSequentially(DelayedRepository(testScheduler), testScheduler)
                .subscribe { list -> actualList.addAll(list) }

        // 24 seconds should pass without any success.
        testScheduler.advanceTimeBy(24, TimeUnit.SECONDS)
        assertTrue(actualList.isEmpty())

        // The stream will emit on the 25th second.
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        assertTrue(actualList.containsAll(setOf(
                Image(1),
                Image(2),
                Image(3),
                Image(4),
                Image(5)
        )))
    }

    inner class DelayedRepository(
            private val delayScheduler: Scheduler = Schedulers.computation()
    ) : ImageRepository {
        override fun downloadById(imageId: Int): Single<Image> =
                Single
                        .just(Image(imageId))
                        .delay(5, TimeUnit.SECONDS, delayScheduler)
    }
}