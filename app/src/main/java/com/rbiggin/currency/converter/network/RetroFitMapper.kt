package com.rbiggin.currency.converter.network

import com.rbiggin.currency.converter.model.CurrencyDto

object RetroFitMapper {
    fun retrofitDtoToCurrencyDto(retrofitDto: RetrofitCurrencyDto): Set<CurrencyDto> {
        val returnedSet = mutableSetOf<CurrencyDto>()

        retrofitDto.rates.forEach{ mapEntry ->
            returnedSet.add(CurrencyDto(retrofitDto.date, mapEntry.key, retrofitDto.base, mapEntry.value))
        }

        return returnedSet
    }
}
