package com.rbiggin.currency.converter.utils

import java.util.*

interface TypedObserver<T> : Observer {

    @Suppress("UNCHECKED_CAST")
    override fun update(observable: Observable?, arg: Any?) {
        if (observable is TypedObservable<*>) {
            (observable.value as? T)?.let {
                onUpdate(it)
            }
        }
    }

    fun onUpdate(value: T)
}
