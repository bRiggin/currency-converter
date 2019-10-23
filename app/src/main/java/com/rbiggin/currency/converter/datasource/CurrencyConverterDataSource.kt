package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.model.CurrencyEntity
import com.rbiggin.currency.converter.utils.TypedObservable

interface CurrencyConverterDataSource {
    val observable: TypedObservable<Map<String, CurrencyEntity>>

    fun setCurrencyCode(code: String)
}