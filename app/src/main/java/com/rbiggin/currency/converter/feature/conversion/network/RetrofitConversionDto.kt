package com.rbiggin.currency.converter.feature.conversion.network

import com.google.gson.annotations.SerializedName

data class RetrofitConversionDto(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: Map<String, Double>
)