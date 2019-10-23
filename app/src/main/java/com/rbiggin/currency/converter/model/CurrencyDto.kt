package com.rbiggin.currency.converter.model

data class CurrencyDto(
    val date: String,
    val nativeCode: String,
    val foreignCode: String,
    val conversionRate: Double
)
