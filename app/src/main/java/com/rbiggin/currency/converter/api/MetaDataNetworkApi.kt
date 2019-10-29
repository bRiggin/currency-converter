package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.network.RetrofitMetaDataNetworkDto

interface MetaDataNetworkApi {
    fun makeCall(
        currencyCode: String,
        success: (String, Set<RetrofitMetaDataNetworkDto>) -> Unit,
        error: ((String, Int?) -> Unit)? = null
    )
}
