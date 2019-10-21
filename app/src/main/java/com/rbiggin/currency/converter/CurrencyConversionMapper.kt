package com.rbiggin.currency.converter

object CurrencyConversionMapper {

    fun convertDtoToEntity(dto: CurrencyDto): CurrencyEntity =
        CurrencyEntity(dto.nativeCode, dto.foreignCode, dto.conversionRate)
}
