package com.rbiggin.currency.converter.api

import android.os.Handler
import com.rbiggin.currency.converter.model.CurrencyDto
import io.mockk.*
import org.junit.Before
import org.junit.Test

class CurrencyConverterServiceTest {

    private val networkApi: CurrencyNetworkApi = mockk(relaxed = true)
    private val handler: Handler = mockk(relaxed = true)

    private lateinit var service: CurrencyConversionApi

    private var updateListener: (Set<CurrencyDto>) -> Unit = mockk()
    private var errorListener: (Int?) -> Unit = mockk()
    private var refreshRunnable: CapturingSlot<Runnable> = slot()

    @Before
    fun `configure currency converter service`() {
        every { updateListener.invoke(any()) } just Runs
        every { errorListener.invoke(any()) } just Runs

        every { handler.postDelayed(capture(refreshRunnable), any()) } returns mockk(relaxed = true)

        service = CurrencyConverterService(networkApi, handler).apply {
            setUpdateListener(updateListener)
            setErrorListener(errorListener)
        }
    }

    @Test
    fun `when currency code is set expect network api to be reconfigured`() {
        val currencyCode = "JPN"
        service.setCurrencyCode(currencyCode)

        verify {
            networkApi.setCurrencyCode(currencyCode)
        }
    }

    @Test
    fun `when currency code is set expect network api call made`() {
        service.setCurrencyCode("")
        verify { networkApi.makeCall(any(), any()) }
    }

    @Test
    fun `when currency code is set expect handler to be cleared`() {
        service.setCurrencyCode("")
        verify { handler.removeCallbacksAndMessages(null) }
    }

    @Test
    fun `when response received from api expect timer to be triggered`() {
        val response: Set<CurrencyDto> = setOf(mockk(), mockk(), mockk())

        every { networkApi.makeCall(captureLambda(), any()) } answers {
            lambda<(Set<CurrencyDto>) -> Unit>().captured.invoke(response)
        }

        service.setCurrencyCode("")

        verify { handler.postDelayed(any(), any()) }
    }

    @Test
    fun `when response received from api expect response listener to be informed`() {
        val response: Set<CurrencyDto> = setOf(mockk(), mockk(), mockk())

        every { networkApi.makeCall(captureLambda(), any()) } answers {
            lambda<(Set<CurrencyDto>) -> Unit>().captured.invoke(response)
        }

        service.setCurrencyCode("")

        verify { updateListener.invoke(any()) }
    }

    @Test
    fun `when response received from api expect handler to be cleared`() {
        val response: Set<CurrencyDto> = setOf(mockk(), mockk(), mockk())

        every { networkApi.makeCall(captureLambda(), any()) } answers {
            lambda<(Set<CurrencyDto>) -> Unit>().captured.invoke(response)
        }

        service.setCurrencyCode("")

        verify { handler.removeCallbacksAndMessages(null) }
    }

    @Test
    fun `when response received from api and refresh timer as timed out expect network api call made`() {
        val response: Set<CurrencyDto> = setOf(mockk(), mockk(), mockk())

        every { networkApi.makeCall(captureLambda(), any()) } answers {
            lambda<(Set<CurrencyDto>) -> Unit>().captured.invoke(response)
        }

        service.setCurrencyCode("")
        refreshRunnable.captured.run()
        verify(exactly = 2) { networkApi.makeCall(any(), any()) }
    }

    @Test
    fun `when error response received from api expect handler to be cleared`() {
        every { networkApi.makeCall(any(), captureLambda()) } answers {
            lambda<(Int) -> Unit>().captured.invoke(mockk(relaxed = true))
        }

        service.setCurrencyCode("")

        verify { handler.removeCallbacksAndMessages(null) }
    }

    @Test
    fun `when error response received from api expect timer to be triggered`() {
        every { networkApi.makeCall(any(), captureLambda()) } answers {
            lambda<(Int) -> Unit>().captured.invoke(mockk(relaxed = true))
        }

        service.setCurrencyCode("")

        verify { handler.postDelayed(any(), any()) }
    }

    @Test
    fun `when error response received from api expect error listener to be informed`() {
        every { networkApi.makeCall(any(), captureLambda()) } answers {
            lambda<(Int) -> Unit>().captured.invoke(mockk(relaxed = true))
        }

        service.setCurrencyCode("")

        verify { errorListener.invoke(any()) }
    }

    @Test
    fun `when error response received and refresh timer as timed out expect network api call made`() {
        every { networkApi.makeCall(any(), captureLambda()) } answers {
            lambda<(Int) -> Unit>().captured.invoke(mockk(relaxed = true))
        }
        service.setCurrencyCode("")
        refreshRunnable.captured.run()
        verify(exactly = 2) { networkApi.makeCall(any(), any()) }
    }
}