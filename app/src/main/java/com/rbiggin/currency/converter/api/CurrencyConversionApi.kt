package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.NetworkCallError
import com.rbiggin.currency.converter.model.CurrencyDto

interface CurrencyConversionApi {

    fun setCurrencyCode(currencyCode: String)

    fun setUpdateListener(listener: (Set<CurrencyDto>) -> Unit)

    fun setErrorListener(listener: (NetworkCallError) -> Unit)
}
