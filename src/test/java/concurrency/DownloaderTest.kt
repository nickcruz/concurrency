package concurrency

import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * Created by Nick Cruz on 9/30/17
 */
class DownloaderTest {

    lateinit var imageDownloader : ImageDownloader

    @Test
    fun concurrentDownload_Takes5Seconds() {
        imageDownloader = ImageDownloader(DelayedRepository())

        val totalTime = measureTimeMillis {
            val actualList = imageDownloader.downloadImages().blockingGet()

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
    fun concurrentDownload_IsInstant() {
        val testScheduler = TestScheduler()
        imageDownloader = ImageDownloader(DelayedRepository(testScheduler), testScheduler)

        val actualList = mutableListOf<Image>()

        imageDownloader.downloadImages()
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
}