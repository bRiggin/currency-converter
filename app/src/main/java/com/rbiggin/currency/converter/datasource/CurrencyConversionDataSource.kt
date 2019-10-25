package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.model.CurrencyConversionEntity
import com.rbiggin.currency.converter.utils.TypedObservable

interface CurrencyConversionDataSource {
    val observable: TypedObservable<Map<String, CurrencyConversionEntity>>

    fun setCurrencyCode(code: String)
}