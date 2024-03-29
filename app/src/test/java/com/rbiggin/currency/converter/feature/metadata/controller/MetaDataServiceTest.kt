package com.rbiggin.currency.converter.feature.metadata.controller

import com.rbiggin.currency.converter.feature.metadata.entity.MetaDataController
import com.rbiggin.currency.converter.model.MetaDataDto
import com.rbiggin.currency.converter.feature.metadata.network.RetrofitMetaDataDto
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MetaDataServiceTest {

    private val api: MetaDataNetworkApi = mockk(relaxed = true)

    private lateinit var service: MetaDataController

    private val eurCurrencyCode = "EUR"

    private var serviceUpdateListener: (MetaDataDto) -> Unit = mockk()
    private var serviceErrorListener: (String, Int?) -> Unit = mockk()

    private var apiUpdateListenerSlot: CapturingSlot<(String, Set<RetrofitMetaDataDto>) -> Unit> =
        slot()
    private var apiErrorListenerSlot: CapturingSlot<(String, Int?) -> Unit> = slot()

    @Before
    fun `configure meta data service`() {
        every { serviceUpdateListener.invoke(any()) } just Runs
        every { serviceErrorListener.invoke(any(), any()) } just Runs

        every {
            api.makeCall(
                any(),
                capture(apiUpdateListenerSlot),
                capture(apiErrorListenerSlot)
            )
        } just Runs

        service = MetaDataService(
            api
        ).apply {
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
    fun `when a network request succeeds and returns empty response expect error listener to be invoked`() {
        service.getMetaData(eurCurrencyCode)
        apiUpdateListenerSlot.captured(eurCurrencyCode, mockk {
            every { isEmpty() } returns true
        })

        verify { serviceErrorListener.invoke(eurCurrencyCode, any()) }
    }

    @Test
    fun `when a network request succeeds expect update`() {
        val update: MutableSet<RetrofitMetaDataDto> = mutableSetOf(
            mockk(relaxed = true) {
                every { population } returns 1000
            },
            mockk(relaxed = true) {
                every { population } returns 2000
            },
            mockk(relaxed = true) {
                every { population } returns 3000
            }
        )

        service.getMetaData(eurCurrencyCode)
        apiUpdateListenerSlot.captured(eurCurrencyCode, update)

        verify { serviceUpdateListener.invoke(any()) }
    }

    @Test
    fun `when a network request succeeds expect update to have correct currency code`() {
        var receivedUpdate: MetaDataDto? = null
        service.setUpdateListener {
            receivedUpdate = it
        }

        val testCurrencyCode = "testCurrencyCode"

        val update: MutableSet<RetrofitMetaDataDto> = mutableSetOf(
            mockk(relaxed = true) {
                every { population } returns 1000
            },
            mockk(relaxed = true) {
                every { population } returns 2000
            },
            mockk(relaxed = true) {
                every { population } returns 3000
                every { currencies } returns arrayOf(hashMapOf("code" to testCurrencyCode))
            }
        )

        service.getMetaData(testCurrencyCode)
        apiUpdateListenerSlot.captured(testCurrencyCode, update)

        assertEquals(testCurrencyCode, receivedUpdate?.currencyCode)
    }

    @Test
    fun `when a network request succeeds expect update to have correct currency name`() {
        var receivedUpdate: MetaDataDto? = null
        service.setUpdateListener {
            receivedUpdate = it
        }

        val testCurrencyCode = "testCurrencyCode"
        val testCurrencyName = "testCurrencyName"

        val update: MutableSet<RetrofitMetaDataDto> = mutableSetOf(
            mockk(relaxed = true) {
                every { population } returns 1000
            },
            mockk(relaxed = true) {
                every { population } returns 2000
            },
            mockk(relaxed = true) {
                every { population } returns 3000
                every { currencies } returns arrayOf(
                    hashMapOf(
                        "code" to testCurrencyCode,
                        "name" to testCurrencyName
                    )
                )
            }
        )

        service.getMetaData(testCurrencyCode)
        apiUpdateListenerSlot.captured(testCurrencyCode, update)

        assertEquals(testCurrencyName, receivedUpdate?.currencyName)
    }

    @Test
    fun `when a network request succeeds expect update to have correct currency flag`() {
        var receivedUpdate: MetaDataDto? = null
        service.setUpdateListener {
            receivedUpdate = it
        }

        val testCurrencyCode = "testCurrencyCode"
        val testCurrencyFlagUrl = "testCurrencyFlagUrl"

        val update: MutableSet<RetrofitMetaDataDto> = mutableSetOf(
            mockk(relaxed = true) {
                every { population } returns 1000
            },
            mockk(relaxed = true) {
                every { population } returns 2000
            },
            mockk(relaxed = true) {
                every { population } returns 3000
                every { flag } returns testCurrencyFlagUrl
                every { currencies } returns arrayOf(hashMapOf("code" to testCurrencyCode))
            }
        )

        service.getMetaData(testCurrencyCode)
        apiUpdateListenerSlot.captured(testCurrencyCode, update)

        assertEquals(testCurrencyFlagUrl, receivedUpdate?.flagUrl)
    }
}