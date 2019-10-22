package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.model.CurrencyDto

interface CurrencyConversionApi {

    fun setCurrencyCode(currencyCode: String)

    fun setOnUpdateListener(listener: (Set<CurrencyDto>) -> Unit)
}
