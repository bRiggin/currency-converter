package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.model.CurrencyMetaDataEntity
import com.rbiggin.currency.converter.model.MetaDataDto

object CurrencyMetaDataMapper {
    fun convertDtoToEntity(dto: MetaDataDto): CurrencyMetaDataEntity =
        CurrencyMetaDataEntity(dto.currencyCode, dto.currencyName, dto.flagUrl)
}
