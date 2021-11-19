package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class PageLoader<out T>(
    private val loader: (Int) -> Single<List<T>>,
    private val pageSize: Int = 20
) {
    private var hasMore = true
    private var currentPage = 0
    private var activeSubscription: Disposable? = null

    private val isLoading  = BehaviorSubject.createDefault(false)
    private val isReset    = BehaviorSubject.createDefault(false)
    private val isError    = BehaviorSubject.createDefault(false)
    private val loadedData = BehaviorSubject.create<List<T>>()

    fun subscribeOnLoadingState(consumer: (Boolean) -> Unit): Disposable = isLoading.subscribe(consumer)

    fun subscribeOnResetState(consumer: (Boolean) -> Unit): Disposable = isReset.subscribe(consumer)

    fun subscribeOnData(consumer: (List<T>) -> Unit): Disposable {
        if (!loadedData.hasValue()) loadNextPage()
        return loadedData.subscribe(consumer)
    }

    fun subscribeOnError(action: () -> Unit): Disposable = isError.subscribeOnAsError(action)

    fun loadNextPage() {
        loadNextPage({ loadedData.value ?: emptyList() }, !isLoading.value!!)
    }

    fun reset() {
        hasMore = true
        currentPage = 0
        activeSubscription?.dispose()

        isReset.onNext(true)

        loadNextPage({ emptyList() }, ableToLoad = true)
    }

    private fun loadNextPage(
        previousDataSupplier: () -> List<T>,
        ableToLoad: Boolean
    ) {
        fun onComplete(error: Boolean) {
            isLoading.onNext(false)
            isReset.onNext(false)
            isError.onNext(error)

            activeSubscription = null
        }

        if (hasMore && ableToLoad) {
            isLoading.onNext(true)
            activeSubscription =
                loader(currentPage).subscribe(
                    {
                        if (it.size < pageSize) {
                            hasMore = false
                        }
                        currentPage++

                        loadedData.onNext(previousDataSupplier() + it)

                        onComplete(false)
                    },
                    { onComplete(true) }
                )
        }
    }
}
