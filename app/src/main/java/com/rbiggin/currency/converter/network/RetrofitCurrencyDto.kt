package com.rbiggin.currency.converter.network

import com.google.gson.annotations.SerializedName

data class RetrofitCurrencyDto(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: Map<String, Double>
)