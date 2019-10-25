package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.model.CurrencyDto
import com.rbiggin.currency.converter.model.CurrencyConversionEntity

object CurrencyConversionMapper {

    fun convertDtoToEntity(dto: CurrencyDto): CurrencyConversionEntity =
        CurrencyConversionEntity(dto.nativeCode, dto.foreignCode, dto.conversionRate)
}
