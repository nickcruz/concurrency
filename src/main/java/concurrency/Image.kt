package concurrency

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

data class Image(val imageId: Int)

interface ImageRepository {
    fun downloadImageById(imageId: Int): Single<Image>
}

fun downloadImages(
        imageRepository: ImageRepository,
        ioScheduler: Scheduler = Schedulers.io()
) : Single<List<Image>> =
        Observable
                .fromArray(1, 2, 3, 4, 5)
                .flatMap { imageRepository.downloadImageById(it)
                        .subscribeOn(ioScheduler)
                        .toObservable() }
                .toList()

fun downloadImagesSequentially(
        imageRepository: ImageRepository,
        ioScheduler: Scheduler = Schedulers.io()
) : Single<List<Image>> =
        Observable
                .fromArray(1, 2, 3, 4, 5)
                .concatMap { imageRepository
                        .downloadImageById(it)
                        .subscribeOn(ioScheduler)
                        .toObservable()
                }
                .toList()