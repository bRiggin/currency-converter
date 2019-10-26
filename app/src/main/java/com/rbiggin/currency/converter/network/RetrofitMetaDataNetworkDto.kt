package com.rbiggin.currency.converter.network

import com.google.gson.annotations.SerializedName

data class RetrofitMetaDataNetworkDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("currencies")
    val currencies: Array<Map<String, String>>,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("population")
    val population: Int
)
