package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.feature.conversion.entity.ConversionController
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionDataSource
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionMapper
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionRepository
import com.rbiggin.currency.converter.model.ConversionDto
import com.rbiggin.currency.converter.model.ConversionEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CurrencyConverterRepositoryTest {

    private val controller: ConversionController = mockk(relaxed = true)
    private val mapper: ConversionMapper = mockk(relaxed = true)

    private lateinit var repository: ConversionDataSource

    private var updateListener: ((Set<ConversionDto>) -> Unit)? = null

    @Before
    fun `configure currency converter repository`() {
        every { controller.setUpdateListener(captureLambda()) } answers {
            updateListener = lambda<((Set<ConversionDto>) -> Unit)>().captured
        }

        repository = ConversionRepository(
            controller,
            mapper
        )
    }

    @Test
    fun `when initialised expect repository to set api with default currency`() {
        verify { controller.setCurrencyCode(ConversionRepository.DEFAULT_CURRENCY_CODE) }
    }

    @Test
    fun `when initialised expect repository to listen for updates from api`() {
        verify { controller.setUpdateListener(any()) }
    }

    @Test
    fun `when update received from api expect mapper to create entity objects`() {
        val update = setOf<ConversionDto>(mockk(), mockk(), mockk())

        updateListener?.invoke(update)

        verify { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api with 1 dto items expect mapper to create 1 entity object`() {
        val update = setOf<ConversionDto>(mockk())

        updateListener?.invoke(update)

        verify(exactly = 1) { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api with 3 dto items expect mapper to create 3 entity objects`() {
        val update = setOf<ConversionDto>(mockk(), mockk(), mockk())

        updateListener?.invoke(update)

        verify(exactly = 3) { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when update received from api with 6 dto items expect mapper to create 6 entity objects`() {
        val update = setOf<ConversionDto>(mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        updateListener?.invoke(update)

        verify(exactly = 6) { mapper.convertDtoToEntity(any()) }
    }

    @Test
    fun `when first update received expect data source to be updated`() {
        val update = setOf<ConversionDto>(
            mockk(relaxed = true)
        )
        every { mapper.convertDtoToEntity(any()) } returns mockk(relaxed = true)

        updateListener?.invoke(update)

        assertNotNull(repository.observable.value)
    }

    @Test
    fun `when update contains new currency expect data source observable to be updated`() {
        val update = setOf<ConversionDto>(mockk())
        val newEntity: ConversionEntity = mockk()
        val newNativeCode = "STL"
        every { newEntity.subjectCode } returns newNativeCode
        every { mapper.convertDtoToEntity(any()) } returns newEntity

        repository.observable.value =
            mapOf(
                "EUR" to ConversionEntity("EUR", "", 0.0),
                "JPN" to ConversionEntity("JPN", "", 0.0),
                "USD" to ConversionEntity("USD", "", 0.0)
            )
        updateListener?.invoke(update)

        assertTrue(repository.observable.value?.containsKey(newNativeCode) == true)
    }

    @Test
    fun `when update contains new conversion rate for currency expect data source observable to be updated`() {
        val update = setOf<ConversionDto>(mockk())
        val newEntity: ConversionEntity = mockk()
        val nativeCode = "EUR"
        val newConversionRate = 7.4
        val oldConversionRate = 7.4
        every { newEntity.subjectCode } returns nativeCode
        every { newEntity.conversionRate } returns newConversionRate
        every { mapper.convertDtoToEntity(any()) } returns newEntity

        repository.observable.value =
            mapOf(nativeCode to ConversionEntity(nativeCode, "", oldConversionRate))
        updateListener?.invoke(update)

        assertEquals(newConversionRate, repository.observable.value?.get(nativeCode)?.conversionRate)
    }

    @Test
    fun `when new currency code is provided expect api to be set with new code`() {
        val newCurrencyCode = "JPN"
        repository.setCurrencyCode(newCurrencyCode)
        verify { controller.setCurrencyCode(newCurrencyCode) }
    }
}