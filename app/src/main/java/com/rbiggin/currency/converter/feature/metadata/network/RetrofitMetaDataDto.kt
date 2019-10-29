package com.rbiggin.currency.converter.feature.metadata.network

import com.google.gson.annotations.SerializedName

data class RetrofitMetaDataDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("currencies")
    val currencies: Array<Map<String, String>>,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("population")
    val population: Int
)
