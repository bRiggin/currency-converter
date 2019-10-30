package com.rbiggin.currency.converter.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import com.rbiggin.currency.converter.utils.TypedObservable
import com.rbiggin.currency.converter.utils.TypedObserver
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CurrencyViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val useCase: CurrencyUseCase = mockk(relaxed = true)

    private lateinit var viewModel: CurrencyViewModel

    private val observable: TypedObservable<Map<String, CurrencyState>> = mockk()

    private var observerSlot: CapturingSlot<TypedObserver<Map<String, CurrencyState>>> = slot()
    private val eurCurrency = CurrencyState("EUR", 1.0)
    private val jpyCurrency = CurrencyState("JPY", 2.0)
    private val usdCurrency = CurrencyState("USD", 3.0)
    private val cadCurrency = CurrencyState("CAD", 4.0)
    private val mxnCurrency = CurrencyState("MXN", 5.0)
    private val czkCurrency = CurrencyState("CZK", 6.0)
    private val defaultUpdate = mutableMapOf(
        eurCurrency.currencyCode to eurCurrency,
        jpyCurrency.currencyCode to jpyCurrency,
        usdCurrency.currencyCode to usdCurrency
    )

    @Before
    fun `configure view model`() {
        every { useCase.currencyStates } returns observable
        every { observable.addTypedObserver(capture(observerSlot)) } just Runs

        viewModel = CurrencyViewModel(useCase)
    }

    @Test
    fun `when initialised expect observer add to use case observable`() {
        verify { observable.addTypedObserver(any()) }
    }

    @Test
    fun `when first currency update is received expect update live data to be populated`() {
        observerSlot.captured.onUpdate(mockk(relaxed = true))
        assertEquals(CurrencyViewModel.UpdateType.InitialUpdate, viewModel.listUpdates.value)
    }

    @Test
    fun `when first currency update is received and list contains EUR expect EUR to be positioned at top`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        assertEquals(eurCurrency.currencyCode, viewModel.conversionList[0].value?.currencyCode)
    }

    @Test
    fun `when currency update is received and no data has changed expect no update`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val originalUpdateType = viewModel.listUpdates.value

        observerSlot.captured.onUpdate(defaultUpdate)
        assertEquals(originalUpdateType, viewModel.listUpdates.value)
    }

    @Test
    fun `when currency update is received and no data has changed expect no change to view model list`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val originalCurrencyList = viewModel.conversionList

        observerSlot.captured.onUpdate(defaultUpdate)
        assertEquals(originalCurrencyList, viewModel.conversionList)
    }

    @Test
    fun `when currency update is received and contains new currency code expect it to be added at the bottom of list`() {
        val update = defaultUpdate.toMutableMap().apply {
            put(cadCurrency.currencyCode, cadCurrency)
        }
        with(observerSlot.captured) {
            onUpdate(defaultUpdate)
            onUpdate(update)
        }

        val lastItemCode =
            viewModel.conversionList[viewModel.conversionList.size - 1].value?.currencyCode

        assertEquals(cadCurrency.currencyCode, lastItemCode)
    }

    @Test
    fun `when currency update is received and contains new currency code expect update type to contain new item`() {
        val update = defaultUpdate.toMutableMap().apply {
            put(cadCurrency.currencyCode, cadCurrency)
        }
        with(observerSlot.captured) {
            onUpdate(defaultUpdate)
            onUpdate(update)
        }

        val itemUpdates = viewModel.listUpdates.value as? CurrencyViewModel.UpdateType.NewItems

        assertEquals(
            CurrencyViewModel.UpdateType.NewItems(defaultUpdate.size, 1), itemUpdates
        )
    }

    @Test
    fun `when currency update is received and contains multiple new currency codes expect them to be added at the bottom of list`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val update = defaultUpdate.toMutableMap().apply {
            put(cadCurrency.currencyCode, cadCurrency)
            put(mxnCurrency.currencyCode, mxnCurrency)
            put(czkCurrency.currencyCode, czkCurrency)
        }
        observerSlot.captured.onUpdate(update)

        assertEquals(update.size, viewModel.conversionList.size)
    }

    @Test
    fun `when use case currency update is received and contains multiple new currency codes expect update type to contain new items`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val update = defaultUpdate.toMutableMap().apply {
            put(cadCurrency.currencyCode, cadCurrency)
            put(mxnCurrency.currencyCode, mxnCurrency)
            put(czkCurrency.currencyCode, czkCurrency)
        }
        observerSlot.captured.onUpdate(update)

        val itemUpdates = viewModel.listUpdates.value as? CurrencyViewModel.UpdateType.NewItems

        assertEquals(
            CurrencyViewModel.UpdateType.NewItems(defaultUpdate.size, 3), itemUpdates
        )
    }

    @Test
    fun `when currency update is received and existing currency codes conversion rate has changed expect value updated`() {
        val testCurrencyCode = jpyCurrency.currencyCode
        val testConversionRate = 2.0

        observerSlot.captured.onUpdate(defaultUpdate)
        val listEntry = viewModel.conversionList.find { it.value?.currencyCode == testCurrencyCode }
        val testIndex = viewModel.conversionList.indexOf(listEntry)

        val update = defaultUpdate.toMutableMap().apply {
            put(jpyCurrency.currencyCode, jpyCurrency.copy(conversionRate = testConversionRate))
        }
        observerSlot.captured.onUpdate(update)

        assertTrue(testConversionRate == viewModel.conversionList[testIndex].value?.conversionRate)
    }

    @Test
    fun `when item is touched at the top of list expect no update`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val originalUpdateType = viewModel.listUpdates.value

        viewModel.onItemTouched(0)
        assertEquals(originalUpdateType, viewModel.listUpdates.value)
    }

    @Test
    fun `when item is touched and index is negative expect no action`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val originalUpdateType = viewModel.listUpdates.value

        viewModel.onItemTouched(-1)
        assertEquals(originalUpdateType, viewModel.listUpdates.value)
    }

    @Test
    fun `when item is touched and index is too large for list size expect no action`() {
        observerSlot.captured.onUpdate(defaultUpdate)
        val originalUpdateType = viewModel.listUpdates.value

        viewModel.onItemTouched(defaultUpdate.size)
        assertEquals(originalUpdateType, viewModel.listUpdates.value)
    }

    @Test
    fun `when item is touched not at the top of list expect item moved to top`() {
        observerSlot.captured.onUpdate(defaultUpdate)

        viewModel.onItemTouched(2)
        assertEquals(
            CurrencyViewModel.UpdateType.NewTopItem(2),
            viewModel.listUpdates.value
        )
    }

    @Test
    fun `when item is touched that isn't at the top of list expect list to be updated`() {
        observerSlot.captured.onUpdate(defaultUpdate)

        val listItemOne = viewModel.conversionList[0]
        val listItemTwo = viewModel.conversionList[1]
        val listItemThree = viewModel.conversionList[2]

        viewModel.onItemTouched(2)
        assertEquals(listItemThree, viewModel.conversionList[0])
        assertEquals(listItemOne, viewModel.conversionList[1])
        assertEquals(listItemTwo, viewModel.conversionList[2])
    }

    @Test
    fun `when a new input value is submitted expect all but top index to be updated`() {
        every { useCase.currencyStates.value } returns defaultUpdate
        observerSlot.captured.onUpdate(defaultUpdate)

        val originalList = mutableListOf<Long>()
        viewModel.conversionList.forEach { listElement ->
            listElement.value?.let { originalList.add(it.value) }
        }

        viewModel.setInputValue(49)

        val updatedList = mutableListOf<Long>()
        viewModel.conversionList.forEach { listElement ->
            listElement.value?.let { updatedList.add(it.value) }
        }

        assertFalse(originalList == updatedList)
    }

    @Test
    fun `when a new input value is submitted expect items to change value`() {
        every { useCase.currencyStates.value } returns defaultUpdate
        observerSlot.captured.onUpdate(defaultUpdate)

        val listItemTwo = viewModel.conversionList[1]
        val listItemThree = viewModel.conversionList[2]

        viewModel.setInputValue(49)

        assertFalse(listItemTwo.value != viewModel.conversionList[1].value)
        assertFalse(listItemThree.value != viewModel.conversionList[2].value)
    }
}