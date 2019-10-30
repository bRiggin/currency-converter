package com.rbiggin.currency.converter.feature.metadata.entity

import com.rbiggin.currency.converter.model.MetaDataEntity
import com.rbiggin.currency.converter.utils.TypedObservable

interface MetaDataDataSource {
    val observable: TypedObservable<Map<String, MetaDataEntity>>

    fun getMetaData(currencyCodes: Set<String>)
}
