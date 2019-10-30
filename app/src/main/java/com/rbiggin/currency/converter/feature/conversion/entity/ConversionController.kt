package com.rbiggin.currency.converter.feature.conversion.entity

import com.rbiggin.currency.converter.model.ConversionDto

interface ConversionController {

    fun setCurrencyCode(currencyCode: String)

    fun setUpdateListener(listener: (Set<ConversionDto>) -> Unit)

    fun setErrorListener(listener: (Int?) -> Unit)
}
