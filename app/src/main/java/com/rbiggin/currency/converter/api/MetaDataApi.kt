package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.model.MetaDataDto

interface MetaDataApi {
    fun getMetaData(currencyCode: String)

    fun setUpdateListener(listener: (MetaDataDto) -> Unit)

    fun setErrorListener(listener: (String, Int?) -> Unit)
}
