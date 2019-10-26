package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.model.CurrencyMetaDataEntity
import com.rbiggin.currency.converter.utils.TypedObservable

interface MetaDataDataSource {
    val observable: TypedObservable<Map<String, CurrencyMetaDataEntity>>

    fun getMetaData(currencyCodes: Set<String>)
}
