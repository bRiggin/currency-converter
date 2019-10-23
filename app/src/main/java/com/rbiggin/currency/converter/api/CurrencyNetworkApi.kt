package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.model.CurrencyDto

interface CurrencyNetworkApi {

    fun setCurrencyCode(code: String)

    fun makeCall(success: (Set<CurrencyDto>) -> Unit, error: ((Int?) -> Unit)? = null)
}
