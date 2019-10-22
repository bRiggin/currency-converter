package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.model.CurrencyDto
import com.rbiggin.currency.converter.model.CurrencyEntity

object CurrencyConversionMapper {

    fun convertDtoToEntity(dto: CurrencyDto): CurrencyEntity =
        CurrencyEntity(dto.nativeCode, dto.foreignCode, dto.conversionRate)
}
