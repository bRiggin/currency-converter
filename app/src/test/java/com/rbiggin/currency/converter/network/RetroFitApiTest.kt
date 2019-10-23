package com.rbiggin.currency.converter.network

import com.rbiggin.currency.converter.api.CurrencyNetworkApi
import com.rbiggin.currency.converter.model.CurrencyDto
import io.mockk.*
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RetroFitApiTest {

    private val retroFitBuilder: Retrofit.Builder = mockk()
    private val mapper: RetroFitMapper = mockk(relaxed = true)

    private lateinit var api: CurrencyNetworkApi

    private val retrofitInstance: Retrofit = mockk(relaxed = true)
    private val client: RetrofitCurrencyClient = mockk(relaxed = true)
    private val call: Call<RetrofitCurrencyDto> = mockk(relaxed = true)

    private val responseListener: (Set<CurrencyDto>) -> Unit = mockk()
    private val errorListener: (Int?) -> Unit = mockk()
    private val callbackSlot: CapturingSlot<Callback<RetrofitCurrencyDto>> = slot()
    private val response: Response<RetrofitCurrencyDto> = mockk(relaxed = true)

    @Before
    fun `configure retrofit api`() {
        every { retroFitBuilder.baseUrl(any<String>()) } returns retroFitBuilder
        every { retroFitBuilder.addConverterFactory(any()) } returns retroFitBuilder
        every { retroFitBuilder.build() } returns retrofitInstance
        every { retrofitInstance.create(RetrofitCurrencyClient::class.java) } returns client
        every { client.getCurrencyConversions(any()) } returns call

        every { call.enqueue(capture(callbackSlot)) } just Runs
        every { responseListener.invoke(any()) } just Runs
        every { errorListener.invoke(any()) } just Runs

        api = RetroFitApi(mapper, retroFitBuilder)
    }

    @Test
    fun `when currency code set expect new retro fit client to be built`() {
        api.setCurrencyCode("")
        verify { retrofitInstance.create(any()) }
    }

    @Test
    fun `when make call is called before currency code is set expect no api call`() {
        api.makeCall(mockk(), mockk())
        verify(exactly = 0) { client.getCurrencyConversions(any()) }
    }

    @Test
    fun `when make call is called after currency code is set expect api call`() {
        api.setCurrencyCode("")
        api.makeCall(mockk(), mockk())
        verify { client.getCurrencyConversions(any()) }
    }

    @Test
    fun `when api response received and is unsuccessful expect error listener invoked`() {
        every { response.isSuccessful } returns false

        api.setCurrencyCode("")
        api.makeCall(responseListener, errorListener)

        callbackSlot.captured.onResponse(mockk(), response)

        verify { errorListener.invoke(any()) }
    }

    @Test
    fun `when api error response received expect error listener invoked`() {
        api.setCurrencyCode("")
        api.makeCall(responseListener, errorListener)

        callbackSlot.captured.onFailure(mockk(), mockk())

        verify { errorListener.invoke(any()) }
    }

    @Test
    fun `when api successful response received expect error listener invoked`() {
        every { response.isSuccessful } returns true
        every { response.body() } returns mockk()

        api.setCurrencyCode("")
        api.makeCall(responseListener, errorListener)

        callbackSlot.captured.onResponse(mockk(), response)

        verify { responseListener.invoke(any()) }
    }

    @Test
    fun `when api successful response received expect mapper requested to transform response to dto`() {
        every { response.isSuccessful } returns true
        every { response.body() } returns mockk()

        api.setCurrencyCode("")
        api.makeCall(responseListener, errorListener)

        callbackSlot.captured.onResponse(mockk(), response)

        verify { mapper.retrofitDtoToCurrencyDto(any()) }
    }
}