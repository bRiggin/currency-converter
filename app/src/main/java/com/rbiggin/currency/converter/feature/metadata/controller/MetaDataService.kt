package com.rbiggin.currency.converter.feature.metadata.controller

import com.rbiggin.currency.converter.feature.metadata.entity.MetaDataController
import com.rbiggin.currency.converter.feature.metadata.network.RetrofitMetaDataDto
import com.rbiggin.currency.converter.model.MetaDataDto

class MetaDataService(private val networkApi: MetaDataNetworkApi) : MetaDataController {

    private var resultPending = false

    private var networkResponseListener: (String, Set<RetrofitMetaDataDto>) -> Unit =
        { code, dtos ->
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
        update: Set<RetrofitMetaDataDto>
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
        dtos: Set<RetrofitMetaDataDto>
    ) {
        var largestDto: RetrofitMetaDataDto? = null
        var largestPopulation = 0
        dtos.forEach {
            if (it.population > largestPopulation) {
                largestPopulation = it.population
                largestDto = it
            }
        }

        largestDto?.let { dto ->
            val currencyName = dto.currencies.find {
                it["code"] == currencyCode
            }?.get("name") ?: ""

            updateListener?.invoke(MetaDataDto(currencyCode, currencyName, dto.flag))
        }
    }

    companion object {
        const val BASE_URL = "https://restcountries.eu/rest/v2/currency/"
    }
}
