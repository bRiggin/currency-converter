package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.NetworkCallError
import com.rbiggin.currency.converter.model.CurrencyDto

interface NetworkApi {

    fun setUrl(url: String)

    fun makeCall(success: (Set<CurrencyDto>) -> Unit, error: ((NetworkCallError) -> Unit)? = null)
}
