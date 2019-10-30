package com.rbiggin.currency.converter.feature.conversion.network

import com.rbiggin.currency.converter.feature.conversion.controller.ConversionService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitConversionClient {

    @GET(ConversionService.RELATIVE_PATH)
    fun getCurrencyConversions(
        @Query(ConversionService.CURRENCY_QUERY_NAME) currencyCode: String
    ): Call<RetrofitConversionDto>
}