package concurrency

import org.junit.Assert.assertTrue
import org.junit.Test
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

    }
}