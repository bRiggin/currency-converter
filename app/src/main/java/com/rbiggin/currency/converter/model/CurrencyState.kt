package com.rbiggin.currency.converter.model

data class CurrencyState(
    val currencyCode: String,
    val conversionRate: Double,
    val currencyName: String? = null,
    val flagAssetUrl: String? = null
) {

    fun foreignToCurrencyCode(foreignValue: Double): Double = foreignValue * conversionRate

    fun currencyCodeToForeign(nativeValue: Double): Double = nativeValue / conversionRate
}
