package com.rbiggin.currency.converter.feature.conversion.controller

import com.rbiggin.currency.converter.model.ConversionDto

interface ConversionNetworkApi {

    fun setCurrencyCode(code: String)

    fun makeCall(success: (Set<ConversionDto>) -> Unit, error: ((Int?) -> Unit)? = null)
}
