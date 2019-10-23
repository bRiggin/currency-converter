package com.rbiggin.currency.converter.network

import com.rbiggin.currency.converter.api.CurrencyConverterService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitCurrencyClient {

    @GET(CurrencyConverterService.RELATIVE_PATH)
    fun getCurrencyConversions(
        @Query(CurrencyConverterService.CURRENCY_QUERY_NAME) currencyCode: String
    ): Call<RetrofitCurrencyDto>
}