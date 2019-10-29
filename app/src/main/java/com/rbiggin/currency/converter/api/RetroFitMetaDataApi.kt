package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.network.RetrofitMetaDataClient
import com.rbiggin.currency.converter.network.RetrofitMetaDataNetworkDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroFitMetaDataApi(
    private val retroFitBuilder: Retrofit.Builder = Retrofit.Builder()
) : MetaDataNetworkApi {

    override fun makeCall(
        currencyCode: String,
        success: (String, Set<RetrofitMetaDataNetworkDto>) -> Unit,
        error: ((String, Int?) -> Unit)?
    ) {
        createNewClient().getMetaData(currencyCode).apply {
            enqueue(object : Callback<Set<RetrofitMetaDataNetworkDto>> {
                override fun onFailure(call: Call<Set<RetrofitMetaDataNetworkDto>>?, t: Throwable?) {
                }

                override fun onResponse(
                    call: Call<Set<RetrofitMetaDataNetworkDto>>?,
                    response: Response<Set<RetrofitMetaDataNetworkDto>>?
                ) {
                    if (response?.isSuccessful == true) {
                        response.body()?.let { success(currencyCode, it) }
                    }
                }
            })
        }
    }


    private fun createNewClient() = retroFitBuilder
        .baseUrl(CurrencyMetaDataService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitMetaDataClient::class.java)
}