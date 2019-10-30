package com.rbiggin.currency.converter.feature.metadata.controller

import com.rbiggin.currency.converter.feature.metadata.network.RetrofitMetaDataDto

interface MetaDataNetworkApi {
    fun makeCall(
        currencyCode: String,
        success: (String, Set<RetrofitMetaDataDto>) -> Unit,
        error: ((String, Int?) -> Unit)? = null
    )
}
