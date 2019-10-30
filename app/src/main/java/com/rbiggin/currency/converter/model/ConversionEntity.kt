package com.rbiggin.currency.converter.model

data class ConversionEntity(
    val subjectCode: String,
    val targetCode: String,
    val conversionRate: Double
)