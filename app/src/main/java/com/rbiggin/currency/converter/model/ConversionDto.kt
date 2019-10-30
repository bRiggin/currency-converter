package com.rbiggin.currency.converter.model

data class ConversionDto(
    val date: String,
    val nativeCode: String,
    val foreignCode: String,
    val conversionRate: Double
)
