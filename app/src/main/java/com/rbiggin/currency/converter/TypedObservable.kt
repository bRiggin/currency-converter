package com.rbiggin.currency.converter

import java.util.Observable

class TypedObservable<T>(initialValue: T? = null) : Observable() {

    var value: T? = initialValue
        set(value) {
            field = value
            setChanged()
            notifyObservers()
        }

    fun addTypedObserver(observer: TypedObserver<T>) {
        super.addObserver(observer)
    }
}
