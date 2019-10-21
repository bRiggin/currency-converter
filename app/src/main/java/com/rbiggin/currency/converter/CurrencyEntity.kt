package com.rbiggin.currency.converter

data class CurrencyEntity(
    val nativeCode: String,
    val foreignCode: String,
    val conversionRate: Double
)
