package com.rbiggin.currency.converter.feature.metadata.network

import com.rbiggin.currency.converter.feature.metadata.controller.MetaDataNetworkApi
import io.mockk.*
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitMetaDataServiceTest {

    private val retroFitBuilder: Retrofit.Builder = mockk()

    private lateinit var api: MetaDataNetworkApi

    private val retrofitInstance: Retrofit = mockk(relaxed = true)
    private val client: RetrofitMetaDataClient = mockk(relaxed = true)
    private val call: Call<Set<RetrofitMetaDataDto>> = mockk(relaxed = true)

    private val responseListener: (String, Set<RetrofitMetaDataDto>) -> Unit = mockk()
    private val errorListener: (String, Int?) -> Unit = mockk()
    private val callbackSlot: CapturingSlot<Callback<Set<RetrofitMetaDataDto>>> = slot()
    private val response: Response<Set<RetrofitMetaDataDto>> = mockk(relaxed = true)

    @Before
    fun `configure retrofit api`() {
        every { retroFitBuilder.baseUrl(any<String>()) } returns retroFitBuilder
        every { retroFitBuilder.addConverterFactory(any()) } returns retroFitBuilder
        every { retroFitBuilder.build() } returns retrofitInstance
        every { retrofitInstance.create(RetrofitMetaDataClient::class.java) } returns client
        every { client.getMetaData(any()) } returns call

        every { call.enqueue(capture(callbackSlot)) } just Runs
        every { responseListener.invoke(any(), any()) } just Runs
        every { errorListener.invoke(any(), any()) } just Runs

        api = RetrofitMetaDataService(retroFitBuilder)
    }

    @Test
    fun `when make call is called expect new retro fit client to be built`() {
        api.makeCall("", mockk(), mockk())
        verify { retrofitInstance.create(any()) }
    }

    @Test
    fun `when make call is called expect api call`() {
        api.makeCall("", mockk(), mockk())
        verify { client.getMetaData(any()) }
    }

    @Test
    fun `when api response received and is unsuccessful expect error listener invoked`() {
        every { response.isSuccessful } returns false

        api.makeCall("", responseListener, errorListener)

        callbackSlot.captured.onResponse(mockk(), response)

        verify { errorListener.invoke(any(), any()) }
    }

    @Test
    fun `when api error response received expect error listener invoked`() {
        api.makeCall("", responseListener, errorListener)

        callbackSlot.captured.onFailure(mockk(), mockk())

        verify { errorListener.invoke(any(), any()) }
    }

    @Test
    fun `when api successful response received expect error listener invoked`() {
        every { response.isSuccessful } returns true
        every { response.body() } returns mockk()

        api.makeCall("", responseListener, errorListener)

        callbackSlot.captured.onResponse(mockk(), response)

        verify { responseListener.invoke(any(), any()) }
    }
}