package com.rbiggin.currency.converter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rbiggin.currency.converter.model.CurrencyModel
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import com.rbiggin.currency.converter.utils.TypedObserver

class CurrencyConversionViewModel(
    private val currencyUseCase: CurrencyUseCase,
    private val mapper: ViewModelMapper = ViewModelMapper
) : ViewModel() {

    private var currentCurrencyCode = "EUR"
        set(value) {
            field = value
            updateStateList(currencyUseCase.currencyStates.value)
        }

    var inputValue: Long = EUROPEAN_DEFAULT_AMOUNT
        set(value) {
            field = value
            updateStateList(currencyUseCase.currencyStates.value)
        }

    private val mutableUpdate = MutableLiveData<UpdateType>()
    val listUpdates: LiveData<UpdateType>
        get() = mutableUpdate

    private var mutableList: MutableList<MutableLiveData<CurrencyModel>> = mutableListOf()
    val conversionList: List<LiveData<CurrencyModel>>
        get() = mutableList

    private val observer = object : TypedObserver<Map<String, CurrencyState>> {
        override fun onUpdate(value: Map<String, CurrencyState>) {
            updateStateList(value)
        }
    }

    init {
        currencyUseCase.currencyStates.addTypedObserver(observer)
    }

    private fun updateStateList(map: Map<String, CurrencyState>?) {
        val isFirstTime = mutableList.isEmpty()

        map?.entries?.forEach { entry ->
            mutableList.find { it.value?.currencyCode == entry.key }?.let {
                updateListItem(it, entry.value)
            } ?: run {
                addNewListItem(entry.value)
            }
        }

        if (isFirstTime) mutableUpdate.postValue(UpdateType.InitialUpdate)
    }

    private fun updateListItem(
        currentLiveData: MutableLiveData<CurrencyModel>,
        newMapEntry: CurrencyState
    ) {
        val isTop = mutableList.indexOf(currentLiveData) == 0
        val newModel = mapper.stateToModel(inputValue, newMapEntry).copy(isTop = isTop)
        currentLiveData.postValue(newModel)
    }

    private fun addNewListItem(
        newMapEntry: CurrencyState
    ) {
        val isFirstItem = mutableList.isEmpty()
        var newModel = mapper.stateToModel(inputValue, newMapEntry)

        if (isFirstItem) newModel = newModel.copy(isTop = true)

        val liveData = MutableLiveData<CurrencyModel>().apply {
            postValue(newModel)
        }
        mutableList.add(liveData)
    }

    fun onItemTouched(index: Int) {
        if (index != 0 && index in 0 until conversionList.size) {
            val element = mutableList[index]
            with(mutableList) {
                remove(element)
                add(0, element)
            }
            currentCurrencyCode = element.value?.currencyCode ?: EUROPEAN_CURRENCY_CODE
            inputValue = element.value?.value ?: EUROPEAN_DEFAULT_AMOUNT
            mutableUpdate.value = UpdateType.NewTopItem(index)
        }
    }

    sealed class UpdateType {
        object InitialUpdate : UpdateType()
        object Pop : UpdateType()
        data class ItemsUpdate(
            val indexesChanged: List<Int>,
            val newItems: NewItems? = null
        ) : UpdateType()

        data class NewTopItem(val fromIndex: Int) : UpdateType()
    }

    data class NewItems(val insertIndex: Int, val numberOfItems: Int)

    companion object {
        private const val EUROPEAN_CURRENCY_CODE = "EUR"
        private const val EUROPEAN_DEFAULT_AMOUNT = 1000L
    }
}
