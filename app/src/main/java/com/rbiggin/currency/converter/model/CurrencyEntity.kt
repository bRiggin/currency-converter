package com.rbiggin.currency.converter.model

data class CurrencyEntity(
    val nativeCode: String,
    val foreignCode: String,
    val conversionRate: Double
)
