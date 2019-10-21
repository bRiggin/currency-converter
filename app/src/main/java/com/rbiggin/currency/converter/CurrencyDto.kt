package com.rbiggin.currency.converter

data class CurrencyDto(
    private val date: String,
    val nativeCode: String,
    val foreignCode: String,
    val conversionRate: Double
)
