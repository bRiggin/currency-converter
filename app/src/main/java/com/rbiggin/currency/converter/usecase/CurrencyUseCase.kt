package com.rbiggin.currency.converter.usecase

import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.utils.TypedObservable

interface CurrencyUseCase {

    val currencyStates: TypedObservable<Map<String, CurrencyState>>
}