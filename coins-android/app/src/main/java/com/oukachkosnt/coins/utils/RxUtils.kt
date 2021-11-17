package com.oukachkosnt.coins.utils

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
