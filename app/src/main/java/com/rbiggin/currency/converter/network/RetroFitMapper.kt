package com.rbiggin.currency.converter.network

import com.rbiggin.currency.converter.model.CurrencyDto

object RetroFitMapper {
    fun retrofitDtoToCurrencyDto(retrofitDto: RetrofitCurrencyDto): Set<CurrencyDto> {
        val returnedSet = mutableSetOf<CurrencyDto>()

        returnedSet.add(CurrencyDto(retrofitDto.date, retrofitDto.base, retrofitDto.base, 1.0))

        retrofitDto.rates.forEach{ mapEntry ->
            returnedSet.add(CurrencyDto(retrofitDto.date, mapEntry.key, retrofitDto.base, mapEntry.value))
        }

        return returnedSet
    }
}
