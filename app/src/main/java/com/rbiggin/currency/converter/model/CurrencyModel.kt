package com.rbiggin.currency.converter.model

data class CurrencyModel(
    val currencyCode: String,
    val conversionRate: Double,
    val value: Double,
    val currencyName: String? = null,
    val flagAssetUrl: String? = null
) {
    fun targetToSubjectCurrency(targetValue: Double): Double = targetValue * conversionRate

    fun subjectCurrencyToTarget(subjectValue: Double): Double = subjectValue / conversionRate
}