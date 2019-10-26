package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.api.MetaDataApi
import com.rbiggin.currency.converter.model.MetaDataDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MetaDataRepositoryTest {

    private val api: MetaDataApi = mockk(relaxed = true)
    private val mapper: CurrencyMetaDataMapper = mockk(relaxed = true)

    private lateinit var repository: MetaDataDataSource

    private var updateListener: ((MetaDataDto) -> Unit)? = null
    private var errorListener: ((String, Int?) -> Unit)? = null

    private val eurCurrencyCode = "EUR"
    private val jpyCurrencyCode = "JPY"
    private val usdCurrencyCode = "USD"

    @Before
    fun `configure meta data repository`() {
        every { api.setUpdateListener(captureLambda()) } answers {
            updateListener = lambda<(MetaDataDto) -> Unit>().captured
        }

        every { api.setErrorListener(captureLambda()) } answers {
            errorListener = lambda<(String, Int?) -> Unit>().captured
        }

        repository = MetaDataRepository(api, mapper)
    }

    @Test
    fun `when initialised expect repository to listen for updates from api`() {
        verify { api.setUpdateListener(any()) }
    }

    @Test
    fun `when initialised expect repository to listen for errors from api`() {
        verify { api.setErrorListener(any()) }
    }

    @Test
    fun `when get data is requested and all codes already in state expect no api call`() {
        repository.observable.value = mapOf(
            eurCurrencyCode to mockk(),
            jpyCurrencyCode to mockk(),
            usdCurrencyCode to mockk()
        )
        repository.getMetaData(setOf(eurCurrencyCode, jpyCurrencyCode, usdCurrencyCode))

        verify(exactly = 0) { api.getMetaData(any()) }
    }

    @Test
    fun `when get data is requested and all codes not in state expect api call`() {
        repository.observable.value = mapOf(
            eurCurrencyCode to mockk(),
            jpyCurrencyCode to mockk()
        )
        repository.getMetaData(setOf(eurCurrencyCode, jpyCurrencyCode, usdCurrencyCode))

        verify { api.getMetaData(usdCurrencyCode) }
    }

    @Test
    fun `when multiple new currencies are request and one is returned from api expect another api call`() {
        repository.getMetaData(setOf(eurCurrencyCode, jpyCurrencyCode))

        verify(exactly = 1) { api.getMetaData(any()) }

        updateListener?.invoke(mockk())

        verify(exactly = 2) { api.getMetaData(any()) }
    }

    @Test
    fun `when update received from api expect mapper used to convert dto to entity`() {
        updateListener?.invoke(mockk(relaxed = true))
        verify { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api expect observer update`() {
        val originalState = repository.observable.value

        updateListener?.invoke(mockk(relaxed = true))

        assertTrue(originalState != repository.observable.value)
    }

    @Test
    fun `when update received from api and all currencies have been acquired expect no further api calls`() {
        repository.getMetaData(setOf(eurCurrencyCode, jpyCurrencyCode))
        updateListener?.invoke(mockk())
        updateListener?.invoke(mockk())
        verify(exactly = 2) { api.getMetaData(any()) }
    }

    @Test
    fun `when api fails to get meta data expect next currency code to be requested`() {
        repository.getMetaData(setOf(eurCurrencyCode, jpyCurrencyCode))
        errorListener?.invoke(eurCurrencyCode, null)
        verify(exactly = 2) { api.getMetaData(any()) }
    }

    @Test
    fun `when api fails to get all requested meta data expect no further api calls`() {
        repository.getMetaData(setOf(eurCurrencyCode, jpyCurrencyCode))
        errorListener?.invoke(eurCurrencyCode, null)
        errorListener?.invoke(jpyCurrencyCode, null)
        verify(exactly = 2) { api.getMetaData(any()) }
    }
}