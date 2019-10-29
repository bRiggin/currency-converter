package com.rbiggin.currency.converter.feature.conversion.entity

import com.rbiggin.currency.converter.model.ConversionDto
import com.rbiggin.currency.converter.model.ConversionEntity

object ConversionMapper {

    fun convertDtoToEntity(dto: ConversionDto): ConversionEntity =
        ConversionEntity(dto.nativeCode, dto.foreignCode, dto.conversionRate)
}
