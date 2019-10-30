package com.rbiggin.currency.converter.feature.conversion.network

import com.rbiggin.currency.converter.feature.conversion.controller.ConversionNetworkApi
import com.rbiggin.currency.converter.feature.conversion.controller.ConversionService
import com.rbiggin.currency.converter.model.ConversionDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConversionService(
    private val mapper: RetrofitConversionMapper = RetrofitConversionMapper,
    private val retroFitBuilder: Retrofit.Builder = Retrofit.Builder()
) : ConversionNetworkApi {

    private var client: RetrofitConversionClient? = null
    private var currencyCode: String? = null

    override fun setCurrencyCode(code: String) {
        createNewClient()
        currencyCode = code
    }

    override fun makeCall(
        success: (Set<ConversionDto>) -> Unit,
        error: ((Int?) -> Unit)?
    ) {
        client?.let { nonNullClient ->
            currencyCode?.let { nonNullCurrencyCode ->
                makeCall(nonNullClient, nonNullCurrencyCode, success, error)
            }
        }
    }

    private fun makeCall(
        client: RetrofitConversionClient,
        currencyCode: String,
        success: (Set<ConversionDto>) -> Unit,
        error: ((Int?) -> Unit)?
    ) {
        client.getCurrencyConversions(currencyCode).apply {
            enqueue(object : Callback<RetrofitConversionDto> {
                override fun onFailure(call: Call<RetrofitConversionDto>?, t: Throwable?) {
                    error?.invoke(null)
                }

                override fun onResponse(
                    call: Call<RetrofitConversionDto>?,
                    response: Response<RetrofitConversionDto>?
                ) {
                    handleNetworkResponse(response, success, error)
                }
            })
        }
    }

    private fun handleNetworkResponse(
        response: Response<RetrofitConversionDto>?,
        success: (Set<ConversionDto>) -> Unit,
        error: ((Int?) -> Unit)?
    ) {
        if (response?.isSuccessful == true) {
            response.body()?.let {
                val currencyDto = mapper.retrofitDtoToCurrencyDto(it)
                success(currencyDto)
            }

        } else {
            error?.invoke(response?.code())
        }
    }

    private fun createNewClient() {
        client = retroFitBuilder
            .baseUrl(ConversionService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitConversionClient::class.java)
    }
}