package com.rbiggin.currency.converter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rbiggin.currency.converter.model.CurrencyModel
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import com.rbiggin.currency.converter.utils.TypedObserver

class CurrencyViewModel(
    private val currencyUseCase: CurrencyUseCase,
    private val mapper: ViewModelMapper = ViewModelMapper
) : ViewModel() {

    private var inputValue: Long = EUROPEAN_DEFAULT_AMOUNT

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

    fun onItemTouched(index: Int) {
        if (index != 0) {
            mutableList.getOrNull(index)?.let { tappedElement ->
                handleTappedElement(tappedElement, index)
            }
        }
    }

    fun setInputValue(newValue: Long, isNewTopItem: Boolean = false) {
        inputValue = newValue
        if (!isNewTopItem) updateStateList(currencyUseCase.currencyStates.value)
    }

    private fun handleTappedElement(tappedElement: MutableLiveData<CurrencyModel>, index: Int) {
        tappedElement.value?.let { currentState ->
            disabledTopItemInput()
            with(mutableList) {
                remove(tappedElement)
                add(0, tappedElement)
            }
            currencyUseCase.setCurrencyCode(currentState.currencyCode)
            setInputValue(currentState.value, true)
            mutableUpdate.value = UpdateType.NewTopItem(index)
        }
    }

    private fun disabledTopItemInput() {
        val topItem = mutableList[0].value
        topItem?.let {
            mutableList[0].postValue(topItem.copy(isTop = false))
        }
    }

    private fun updateStateList(map: Map<String, CurrencyState>?) {
        val isFirstTime = mutableList.isEmpty()
        var numberOfNewItems = 0

        map?.entries?.forEach { entry ->
            mutableList.find { it.value?.currencyCode == entry.key }?.let {
                updateListItem(it, entry.value)
            } ?: run {
                addNewListItem(entry.value)
                numberOfNewItems++
            }
        }

        publishUpdate(isFirstTime, numberOfNewItems)
    }

    private fun publishUpdate(isFirstTime: Boolean, numberOfNewItems: Int) {
        with(mutableUpdate) {
            if (isFirstTime) {
                postValue(UpdateType.InitialUpdate)
            } else if (numberOfNewItems > 0) {
                postValue(
                    UpdateType.NewItems(mutableList.size - numberOfNewItems, numberOfNewItems)
                )
            }
        }
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

    sealed class UpdateType {
        object InitialUpdate : UpdateType()
        data class NewItems(val insertIndex: Int, val numberOfItems: Int) : UpdateType()
        data class NewTopItem(val fromIndex: Int) : UpdateType()
    }

    companion object {
        private const val EUROPEAN_DEFAULT_AMOUNT = 1000L
    }
}
