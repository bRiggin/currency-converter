package com.rbiggin.currency.converter.model

data class CurrencyModel(
    val currencyCode: String,
    val conversionRate: Double,
    val value: Long,
    val currencyName: String? = null,
    val flagAssetUrl: String? = null,
    val isTop: Boolean = false
)