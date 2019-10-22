package com.rbiggin.currency.converter.api

import android.os.Handler
import com.rbiggin.currency.converter.NetworkCallError
import com.rbiggin.currency.converter.model.CurrencyDto

class CurrencyConverterService(
    private val networkApi: NetworkApi,
    private val handler: Handler = Handler()
) : CurrencyConversionApi {

    private var networkResponseListener: (Set<CurrencyDto>) -> Unit = { dtos ->
        handleNetworkResponse(dtos)
    }

    private var networkErrorListener: (NetworkCallError) -> Unit = { error ->
        handleNetworkError(error)
    }

    private val refreshRunnable = Runnable {
        makeNetworkCall()
    }

    private var updateListener: ((Set<CurrencyDto>) -> Unit)? = null
    private var errorListener: ((NetworkCallError) -> Unit)? = null

    override fun setCurrencyCode(currencyCode: String) {
        clearHandler()
        networkApi.setUrl(BASE_URL + CURRENCY_CODE_SUFFIX + currencyCode)
        makeNetworkCall()
    }

    override fun setUpdateListener(listener: (Set<CurrencyDto>) -> Unit) {
        updateListener = listener
    }

    override fun setErrorListener(listener: (NetworkCallError) -> Unit) {
        errorListener = listener
    }

    private fun handleNetworkError(error: NetworkCallError) {
        errorListener?.invoke(error)
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
        const val BASE_URL = "https://revolut.duckdns.org/latest"
        const val CURRENCY_CODE_SUFFIX = "?base="
        private const val REFRESH_PERIOD_MS = 1000L
    }
}