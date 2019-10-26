package com.rbiggin.currency.converter.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitMetaDataClient {
    @GET("{currency}")
    fun getMetaData(@Path("currency") currencyCode: String): Call<Set<RetrofitMetaDataNetworkDto>>
}