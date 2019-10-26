package com.rbiggin.currency.converter.model

data class CurrencyModel(
    val currencyCode: String,
    val conversionRate: Double,
    val value: Int,
    val currencyName: String? = null,
    val flagAssetUrl: String? = null
) {
    fun targetToSubjectCurrency(targetValue: Double): Int = (targetValue * conversionRate).toInt()

    fun subjectCurrencyToTarget(subjectValue: Double): Int = (subjectValue / conversionRate).toInt()
}