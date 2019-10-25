package com.rbiggin.currency.converter.model

data class CurrencyConversionEntity(
    val nativeCode: String,
    val foreignCode: String,
    val conversionRate: Double
)