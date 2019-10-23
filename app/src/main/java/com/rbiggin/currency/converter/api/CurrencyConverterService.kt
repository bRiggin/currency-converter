package com.rbiggin.currency.converter.api

import android.os.Handler
import com.rbiggin.currency.converter.model.CurrencyDto

class CurrencyConverterService(
    private val networkApi: CurrencyNetworkApi,
    private val handler: Handler = Handler()
) : CurrencyConversionApi {

    private var networkResponseListener: (Set<CurrencyDto>) -> Unit = { dtos ->
        handleNetworkResponse(dtos)
    }

    private var networkErrorListener: (Int?) -> Unit = { error ->
        handleNetworkError(error)
    }

    private val refreshRunnable = Runnable {
        makeNetworkCall()
    }

    private var updateListener: ((Set<CurrencyDto>) -> Unit)? = null
    private var errorListener: ((Int?) -> Unit)? = null

    override fun setCurrencyCode(currencyCode: String) {
        clearHandler()
        networkApi.setCurrencyCode(currencyCode)
        makeNetworkCall()
    }

    override fun setUpdateListener(listener: (Set<CurrencyDto>) -> Unit) {
        updateListener = listener
    }

    override fun setErrorListener(listener: (Int?) -> Unit) {
        errorListener = listener
    }

    private fun handleNetworkError(errorCode: Int?) {
        errorListener?.invoke(errorCode)
        startRefreshTimer()
    }

    private fun handleNetworkResponse(update: Set<CurrencyDto>) {
        updateListener?.invoke(update)
        startRefreshTimer()
    }

    private fun startRefreshTimer() {
        clearHandler()
        handler.postDelayed(refreshRunnable, REFRESH_PERIOD_MS)
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