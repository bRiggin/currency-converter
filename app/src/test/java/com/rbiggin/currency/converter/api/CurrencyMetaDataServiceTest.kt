package com.rbiggin.currency.converter.api

import com.rbiggin.currency.converter.model.MetaDataDto
import com.rbiggin.currency.converter.network.RetrofitMetaDataNetworkDto
import io.mockk.*
import org.junit.Before
import org.junit.Test

class CurrencyMetaDataServiceTest {

    private val api: MetaDataNetworkApi = mockk(relaxed = true)

    private lateinit var service: MetaDataApi

    private val eurCurrencyCode = "EUR"

    private var serviceUpdateListener: (MetaDataDto) -> Unit = mockk()
    private var serviceErrorListener: (String, Int?) -> Unit = mockk()

    private var apiUpdateListenerSlot: CapturingSlot<(String, Set<RetrofitMetaDataNetworkDto>) -> Unit> = slot()
    private var apiErrorListenerSlot: CapturingSlot<(String, Int?) -> Unit> = slot()

    @Before
    fun `configure meta data service`() {
        every { serviceUpdateListener.invoke(any()) } just Runs
        every { serviceErrorListener.invoke(any(), any()) } just Runs

        every { api.makeCall(any(), capture(apiUpdateListenerSlot), capture(apiErrorListenerSlot)) } just Runs

        service = CurrencyMetaDataService(api).apply {
            setUpdateListener(serviceUpdateListener)
            setErrorListener(serviceErrorListener)
        }
    }

    @Test
    fun `when requested to make network call expect call to api`() {
        service.getMetaData(eurCurrencyCode)
        verify { api.makeCall(eurCurrencyCode, any(), any()) }
    }

    @Test
    fun `when requested to make network call before previous response received expect no api call`() {
        service.getMetaData(eurCurrencyCode)
        service.getMetaData(eurCurrencyCode)
        verify(exactly = 1) { api.makeCall(eurCurrencyCode, any(), any()) }
    }

    @Test
    fun `when requested to make network call after previous response received expect api call`() {
        service.getMetaData(eurCurrencyCode)
        apiUpdateListenerSlot.invoke("", mockk(relaxed = true))
        service.getMetaData(eurCurrencyCode)
        verify(exactly = 2) { api.makeCall(eurCurrencyCode, any(), any()) }
    }

    @Test
    fun `when a network request fails expect the service error listener to be invoked`() {
        service.getMetaData(eurCurrencyCode)
        apiErrorListenerSlot.captured(eurCurrencyCode, null)
        verify { serviceErrorListener.invoke(eurCurrencyCode, any()) }
    }

    @Test
    fun `when a network request succeeds and returns empty response  expect error listener to be invoked`() {
        service.getMetaData(eurCurrencyCode)
        apiUpdateListenerSlot.captured(eurCurrencyCode, mockk {
            every { isEmpty() } returns true
        })

        verify { serviceErrorListener.invoke(eurCurrencyCode, any()) }
    }

}