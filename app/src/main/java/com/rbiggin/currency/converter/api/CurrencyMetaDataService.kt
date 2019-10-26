package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.model.MetaDataDto
import com.rbiggin.currency.converter.network.RetrofitMetaDataNetworkDto

class CurrencyMetaDataService(
    private val networkApi: MetaDataNetworkApi
) : MetaDataApi {

    private var resultPending = false

    private var networkResponseListener: (String, Set<RetrofitMetaDataNetworkDto>) -> Unit = { code, dtos ->
        handleNetworkResponse(code, dtos)
    }

    private var networkErrorListener: (String, Int?) -> Unit = { currencyCode, error ->
        handleNetworkError(currencyCode, error)
    }

    private var updateListener: ((MetaDataDto) -> Unit)? = null
    private var errorListener: ((String, Int?) -> Unit)? = null

    override fun setUpdateListener(listener: (MetaDataDto) -> Unit) {
        updateListener = listener
    }

    override fun setErrorListener(listener: (String, Int?) -> Unit) {
        errorListener = listener
    }

    override fun getMetaData(currencyCode: String) {
        makeNetworkCall(currencyCode)
    }

    private fun makeNetworkCall(currencyCode: String) {
        if (!resultPending) {
            networkApi.makeCall(currencyCode, networkResponseListener, networkErrorListener)
            resultPending = true
        }
    }

    private fun handleNetworkError(currencyCode: String, errorCode: Int?) {
        resultPending = false
        errorListener?.invoke(currencyCode, errorCode)
    }

    private fun handleNetworkResponse(
        currencyCode: String,
        update: Set<RetrofitMetaDataNetworkDto>
    ) {
        resultPending = false
        if (update.isEmpty()) {
            errorListener?.invoke(currencyCode, null)
        } else {
            identifyLargestDto(currencyCode, update)
        }
    }

    private fun identifyLargestDto(
        currencyCode: String,
        dtos: Set<RetrofitMetaDataNetworkDto>
    ) {
        var largestDto: RetrofitMetaDataNetworkDto? = null
        var largestPopulation = 0
        dtos.forEach {
            if (it.population > largestPopulation) {
                largestPopulation = it.population
                largestDto = it
            }
        }

        largestDto?.let {
            updateListener?.invoke(MetaDataDto(currencyCode, it.name, it.flag))
        }
    }
}
