package com.rbiggin.currency.converter.feature.conversion.controller

import android.os.Handler
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionController
import com.rbiggin.currency.converter.model.ConversionDto

class ConversionService(
    private val networkApi: ConversionNetworkApi,
    private val handler: Handler = Handler()
) : ConversionController {

    private var networkResponseListener: (Set<ConversionDto>) -> Unit = { dtos ->
        handleNetworkResponse(dtos)
    }

    private var networkErrorListener: (Int?) -> Unit = { error ->
        handleNetworkError(error)
    }

    private val refreshRunnable = Runnable {
        makeNetworkCall()
    }

    private var updateListener: ((Set<ConversionDto>) -> Unit)? = null
    private var errorListener: ((Int?) -> Unit)? = null

    override fun setCurrencyCode(currencyCode: String) {
        clearHandler()
        networkApi.setCurrencyCode(currencyCode)
        makeNetworkCall()
    }

    override fun setUpdateListener(listener: (Set<ConversionDto>) -> Unit) {
        updateListener = listener
    }

    override fun setErrorListener(listener: (Int?) -> Unit) {
        errorListener = listener
    }

    private fun handleNetworkError(errorCode: Int?) {
        errorListener?.invoke(errorCode)
        startRefreshTimer()
    }

    private fun handleNetworkResponse(update: Set<ConversionDto>) {
        updateListener?.invoke(update)
        startRefreshTimer()
    }

    private fun startRefreshTimer() {
        clearHandler()
        handler.postDelayed(refreshRunnable,
            REFRESH_PERIOD_MS
        )
    }

    private fun makeNetworkCall() {
        networkApi.makeCall(networkResponseListener, networkErrorListener)
    }

    private fun clearHandler() {
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val BASE_URL = "https://revolut.duckdns.org/"
        const val RELATIVE_PATH = "/latest"
        const val CURRENCY_QUERY_NAME = "base"
        private const val REFRESH_PERIOD_MS = 1000L
    }
}