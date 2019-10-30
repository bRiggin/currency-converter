package com.rbiggin.currency.converter.feature.metadata.entity

import com.rbiggin.currency.converter.model.MetaDataDto

interface MetaDataController {
    fun getMetaData(currencyCode: String)

    fun setUpdateListener(listener: (MetaDataDto) -> Unit)

    fun setErrorListener(listener: (String, Int?) -> Unit)
}
