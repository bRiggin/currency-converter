package com.rbiggin.currency.converter.network

import com.rbiggin.currency.converter.api.CurrencyConverterService
import com.rbiggin.currency.converter.api.CurrencyNetworkApi
import com.rbiggin.currency.converter.model.CurrencyDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroFitApi(
    private val mapper: RetroFitMapper = RetroFitMapper,
    private val retroFitBuilder: Retrofit.Builder = Retrofit.Builder()
) : CurrencyNetworkApi {

    private var client: RetrofitCurrencyClient? = null
    private var currencyCode: String? = null

    override fun setCurrencyCode(code: String) {
        createNewClient()
        currencyCode = code
    }

    override fun makeCall(
        success: (Set<CurrencyDto>) -> Unit,
        error: ((Int?) -> Unit)?
    ) {
        client?.let { nonNullClient ->
            currencyCode?.let { nonNullCurrencyCode ->
                makeCall(nonNullClient, nonNullCurrencyCode, success, error)
            }
        }
    }

    private fun makeCall(
        client: RetrofitCurrencyClient,
        currencyCode: String,
        success: (Set<CurrencyDto>) -> Unit,
        error: ((Int?) -> Unit)?
    ) {
        client.getCurrencyConversions(currencyCode).apply {
            enqueue(object : Callback<RetrofitCurrencyDto> {
                override fun onFailure(call: Call<RetrofitCurrencyDto>?, t: Throwable?) {
                    error?.invoke(null)
                }

                override fun onResponse(
                    call: Call<RetrofitCurrencyDto>?,
                    response: Response<RetrofitCurrencyDto>?
                ) {
                    handleNetworkResponse(response, success, error)
                }
            })
        }
    }

    private fun handleNetworkResponse(
        response: Response<RetrofitCurrencyDto>?,
        success: (Set<CurrencyDto>) -> Unit,
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
            .baseUrl(CurrencyConverterService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitCurrencyClient::class.java)
    }
}