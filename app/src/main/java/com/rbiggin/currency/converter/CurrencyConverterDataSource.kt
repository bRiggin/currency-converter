package com.rbiggin.currency.converter

interface CurrencyConverterDataSource {
    val observable: TypedObservable<Map<String, CurrencyEntity>>
}