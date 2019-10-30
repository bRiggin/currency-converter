package com.rbiggin.currency.converter.feature.conversion.network

import com.rbiggin.currency.converter.model.ConversionDto

object RetrofitConversionMapper {
    fun retrofitDtoToCurrencyDto(retrofitDto: RetrofitConversionDto): Set<ConversionDto> {
        val returnedSet = mutableSetOf<ConversionDto>()

        returnedSet.add(ConversionDto(retrofitDto.date, retrofitDto.base, retrofitDto.base, 1.0))

        retrofitDto.rates.forEach { mapEntry ->
            returnedSet.add(ConversionDto(retrofitDto.date, mapEntry.key, retrofitDto.base, mapEntry.value))
        }

        return returnedSet
    }
}
