package com.oukachkosnt.coins.utils

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

fun BehaviorSubject<Boolean>.subscribeOnAsError(onError: () -> Unit): Disposable {
    return subscribe {
       if (it) {
           onError()
           onNext(false)
       }
    }
}

fun <T, U> zipSingles(firstSingle: Single<T>, secondSingle: Single<U>): Single<Pair<T, U>> {
    return Single.zip(firstSingle, secondSingle) { t, u -> t to u }
}
