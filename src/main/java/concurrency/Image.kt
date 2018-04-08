package concurrency

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

data class Image(val imageId: Int)

interface ImageRepository {
    fun downloadById(imageId: Int): Single<Image>
}

fun downloadImages(
        repo: ImageRepository,
        scheduler: Scheduler = Schedulers.io()
) : Single<List<Image>> =
        Observable
                .fromArray(1, 2, 3, 4, 5)
                .flatMap { repo.downloadById(it)
                        .subscribeOn(scheduler)
                        .toObservable() }
                .toList()

fun downloadImagesSequentially(
        repo: ImageRepository,
        scheduler: Scheduler = Schedulers.io()
) : Single<List<Image>> =
        Observable
                .fromArray(1, 2, 3, 4, 5)
                .concatMap { repo
                        .downloadById(it)
                        .subscribeOn(scheduler)
                        .toObservable()
                }
                .toList()