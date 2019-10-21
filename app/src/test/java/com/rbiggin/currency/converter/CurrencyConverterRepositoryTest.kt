package com.rbiggin.currency.converter

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CurrencyConverterRepositoryTest {

    private val api: CurrencyConversionApi = mockk(relaxed = true)
    private val mapper: CurrencyConversionMapper = mockk(relaxed = true)

    private lateinit var repository: CurrencyConverterRepository

    private var updateListener: ((Set<CurrencyDto>) -> Unit)? = null

    @Before
    fun `configure currency converter repository`() {
        every { api.setOnUpdateListener(captureLambda()) } answers {
            updateListener = lambda<((Set<CurrencyDto>) -> Unit)>().captured
        }

        repository = CurrencyConverterRepository(api, mapper)
    }

    @Test
    fun `when initialised expect repository to set api with default currency`() {
        verify { api.setCurrencyCode(CurrencyConverterRepository.DEFAULT_CURRENCY_CODE) }
    }

    @Test
    fun `when initialised expect repository to listen for updates from api`() {
        verify { api.setOnUpdateListener(any()) }
    }

    @Test
    fun `when update received from api expect mapper to create entity objects`() {
        val update = setOf<CurrencyDto>(mockk(), mockk(), mockk())

        updateListener?.invoke(update)

        verify { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api with 1 dto items expect mapper to create 1 entity object`() {
        val update = setOf<CurrencyDto>(mockk())

        updateListener?.invoke(update)

        verify(exactly = 1) { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api with 3 dto items expect mapper to create 3 entity objects`() {
        val update = setOf<CurrencyDto>(mockk(), mockk(), mockk())

        updateListener?.invoke(update)

        verify(exactly = 3) { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api with 6 dto items expect mapper to create 6 entity objects`() {
        val update = setOf<CurrencyDto>(mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        updateListener?.invoke(update)

        verify(exactly = 6) { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when first update received expect data source to be updated`() {
        val update = setOf<CurrencyDto>(
            mockk(relaxed = true)
        )
        every { mapper.convertDtoToEntity(any()) } returns mockk(relaxed = true)

        updateListener?.invoke(update)

        assertNotNull(repository.observable.value)
    }

    @Test
    fun `when update contains new currency expect data source observable to be updated`() {
        val update = setOf<CurrencyDto>(mockk())
        val newEntity: CurrencyEntity = mockk()
        val newNativeCode = "STL"
        every { newEntity.nativeCode } returns newNativeCode
        every { mapper.convertDtoToEntity(any()) } returns newEntity

        repository.observable.value =
            mapOf(
                "EUR" to CurrencyEntity("EUR", "", 0.0),
                "JPN" to CurrencyEntity("JPN", "", 0.0),
                "USD" to CurrencyEntity("USD", "", 0.0)
            )
        updateListener?.invoke(update)

        assertTrue(repository.observable.value?.containsKey(newNativeCode) == true)
    }

    @Test
    fun `when update contains new conversion rate for currency expect data source observable to be updated`() {
        val update = setOf<CurrencyDto>(mockk())
        val newEntity: CurrencyEntity = mockk()
        val nativeCode = "EUR"
        val newConversionRate = 7.4
        val oldConversionRate = 7.4
        every { newEntity.nativeCode } returns nativeCode
        every { newEntity.conversionRate } returns newConversionRate
        every { mapper.convertDtoToEntity(any()) } returns newEntity

        repository.observable.value =
            mapOf(nativeCode to CurrencyEntity(nativeCode, "", oldConversionRate))
        updateListener?.invoke(update)

        assertEquals(newConversionRate, repository.observable.value?.get(nativeCode)?.conversionRate)
    }

    @Test
    fun `when new currency code is provided expect api to be set with new code`() {
        val newCurrencyCode = "JPN"
        repository.setCurrencyCode(newCurrencyCode)
        verify { api.setCurrencyCode(newCurrencyCode) }
    }
}