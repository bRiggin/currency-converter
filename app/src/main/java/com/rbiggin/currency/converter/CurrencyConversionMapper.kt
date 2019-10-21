package com.rbiggin.currency.converter

object CurrencyConversionMapper {

    fun convertDtoToEntity(dto: CurrencyDto): CurrencyEntity {
        return CurrencyEntity("", "", 0.0)
    }
}
