package com.rbiggin.currency.converter

interface CurrencyConversionApi {

    fun setCurrencyCode(currencyCode: String)

    fun setOnUpdateListener(listener: (Set<CurrencyDto>) -> Unit)
}
