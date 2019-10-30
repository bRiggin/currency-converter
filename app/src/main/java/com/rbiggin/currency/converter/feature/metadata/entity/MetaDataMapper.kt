package com.rbiggin.currency.converter.feature.metadata.entity

import com.rbiggin.currency.converter.model.MetaDataEntity
import com.rbiggin.currency.converter.model.MetaDataDto

object MetaDataMapper {
    fun convertDtoToEntity(dto: MetaDataDto): MetaDataEntity =
        MetaDataEntity(dto.currencyCode, dto.currencyName, dto.flagUrl)
}
