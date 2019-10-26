package com.rbiggin.currency.converter.model

data class CurrencyConversionEntity(
    val subjectCode: String,
    val targetCode: String,
    val conversionRate: Double
)