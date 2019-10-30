package com.rbiggin.currency.converter.feature.conversion.entity

import com.rbiggin.currency.converter.model.ConversionEntity
import com.rbiggin.currency.converter.utils.TypedObservable

interface ConversionDataSource {
    val observable: TypedObservable<Map<String, ConversionEntity>>

    fun setCurrencyCode(code: String)
}