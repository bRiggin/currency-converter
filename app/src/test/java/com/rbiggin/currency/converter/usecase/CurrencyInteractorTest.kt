package com.rbiggin.currency.converter.usecase

import com.rbiggin.currency.converter.datasource.CurrencyConversionDataSource
import com.rbiggin.currency.converter.model.CurrencyConversionEntity
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.utils.TypedObservable
import com.rbiggin.currency.converter.utils.TypedObserver
import io.mockk.*
import junit.framework.Assert.assertNull
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CurrencyInteractorTest {

    private val conversionDataSource: CurrencyConversionDataSource = mockk()

    private lateinit var useCase: CurrencyUseCase

    private val conversionObservable: TypedObservable<Map<String, CurrencyConversionEntity>> = mockk(relaxed = true)
    private val conversionState: Map<String, CurrencyConversionEntity> = mockk()

    private val conversionObserverSlot: CapturingSlot<TypedObserver<Map<String, CurrencyConversionEntity>>> = slot()
    private var conversionObserver: TypedObserver<Map<String, CurrencyConversionEntity>>? = null

    private val cadCurrencyCode: String = "CAD"

    private val eurCurrencyCode: String = "EUR"
    private val eurCurrencyConversionRate: Double = 5.0
    private val eurMock: CurrencyState = mockk(relaxed = true) {
        every { currencyCode } returns eurCurrencyCode
    }

    private val jpyCurrencyCode: String = "JPY"
    private val jpyMock: CurrencyState = mockk(relaxed = true) {
        every { currencyCode } returns jpyCurrencyCode
    }

    private val usdCurrencyCode: String = "USD"
    private val usdMock: CurrencyState = mockk(relaxed = true) {
        every { currencyCode } returns usdCurrencyCode
    }

    private val defaultUseCaseState = mapOf(
        eurCurrencyCode to eurMock,
        jpyCurrencyCode to jpyMock,
        usdCurrencyCode to usdMock
    )

    @Before
    fun `configure currency use case`() {
        every { conversionDataSource.observable } returns conversionObservable
        every { conversionObservable.value } returns conversionState
        every { conversionObservable.addTypedObserver(capture(conversionObserverSlot)) } answers {
            conversionObserver = conversionObserverSlot.captured
        }

        useCase = CurrencyInteractor(conversionDataSource)
    }

    @Test
    fun `when initialised expect observer added to conversion data source`() {
        verify { conversionObservable.addTypedObserver(any()) }
    }

    @Test
    fun `when a conversion update received and update is empty expect no update`() {
        assertNull(useCase.currencyStates.value)
        conversionObserver?.onUpdate(mapOf())
        assertNull(useCase.currencyStates.value)
    }

    @Test
    fun `when when conversion update received expect use case state to be updated`() {
        assertNull(useCase.currencyStates.value)
        conversionObserver?.onUpdate(
            mapOf(
                eurCurrencyCode to mockk(relaxed = true),
                jpyCurrencyCode to mockk(relaxed = true),
                usdCurrencyCode to mockk(relaxed = true)
            )
        )
        assertNotNull(useCase.currencyStates.value)
    }

    @Test
    fun `when when conversion update received and contains new currency code expect code to be added`() {
        useCase.currencyStates.value = defaultUseCaseState
        conversionObserver?.onUpdate(
            mapOf(
                eurCurrencyCode to mockk(relaxed = true),
                jpyCurrencyCode to mockk(relaxed = true),
                usdCurrencyCode to mockk(relaxed = true),
                cadCurrencyCode to mockk(relaxed = true)
            )
        )
        assertTrue(useCase.currencyStates.value?.containsKey(cadCurrencyCode) == true)
    }

    @Test
    fun `when when conversion update received and contains currency code with new conversion rate expect rate to be added`() {
        every { eurMock.conversionRate } returns eurCurrencyConversionRate
        val newConversionRate = eurCurrencyConversionRate + 1.0
        useCase.currencyStates.value = defaultUseCaseState
        conversionObserver?.onUpdate(
            mapOf(
                eurCurrencyCode to mockk(relaxed = true) {
                    every { conversionRate } returns  newConversionRate
                },
                jpyCurrencyCode to mockk(relaxed = true),
                usdCurrencyCode to mockk(relaxed = true)
            )
        )
        assertFalse(useCase.currencyStates.value?.get(eurCurrencyCode)?.conversionRate == eurCurrencyConversionRate)
    }
}