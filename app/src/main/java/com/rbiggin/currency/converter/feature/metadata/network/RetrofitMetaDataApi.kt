package com.rbiggin.currency.converter.feature.metadata.network

import com.rbiggin.currency.converter.feature.metadata.controller.MetaDataService
import com.rbiggin.currency.converter.feature.metadata.controller.MetaDataNetworkApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitMetaDataApi(
    private val retroFitBuilder: Retrofit.Builder = Retrofit.Builder()
) : MetaDataNetworkApi {

    override fun makeCall(
        currencyCode: String,
        success: (String, Set<RetrofitMetaDataDto>) -> Unit,
        error: ((String, Int?) -> Unit)?
    ) {
        createNewClient().getMetaData(currencyCode).apply {
            enqueue(object : Callback<Set<RetrofitMetaDataDto>> {
                override fun onFailure(call: Call<Set<RetrofitMetaDataDto>>?, t: Throwable?) {
                }

                override fun onResponse(
                    call: Call<Set<RetrofitMetaDataDto>>?,
                    response: Response<Set<RetrofitMetaDataDto>>?
                ) {
                    if (response?.isSuccessful == true) {
                        response.body()?.let { success(currencyCode, it) }
                    }
                }
            })
        }
    }


    private fun createNewClient() = retroFitBuilder
        .baseUrl(MetaDataService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitMetaDataClient::class.java)
}