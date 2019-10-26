package com.rbiggin.currency.converter.model

data class CurrencyState(
    val currencyCode: String,
    val conversionRate: Double,
    val currencyName: String? = null,
    val flagAssetUrl: String? = null
) {
    fun subjectCurrencyToTarget(subjectValue: Int): Int = (subjectValue / conversionRate).toInt()
}
