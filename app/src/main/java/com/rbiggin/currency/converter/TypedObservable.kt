package com.rbiggin.currency.converter

import java.util.Observable

class TypedObservable<T>(initialValue: T? = null) : Observable() {

    var value: T? = null

    init {
        initialValue?.let { value = it }
    }
}
