package com.rbiggin.currency.converter.presentation

import android.util.Log
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

    private var inputValue: Long = EUROPEAN_DEFAULT_AMOUNT

    private val mutableUpdate = MutableLiveData<UpdateType>()
    val listUpdates: LiveData<UpdateType>
        get() = mutableUpdate

    private var mutableList: MutableList<MutableLiveData<CurrencyModel>> = mutableListOf()
    val conversionList: List<LiveData<CurrencyModel>>
        get() = mutableList

    private val observer = object : TypedObserver<Map<String, CurrencyState>> {
        override fun onUpdate(value: Map<String, CurrencyState>) {
            Log.i("----FROM OBSERVER----", "Calling update list. Value: $inputValue")
            updateStateList(value)
        }
    }

    init {
        currencyUseCase.currencyStates.addTypedObserver(observer)
    }

    fun onItemTouched(index: Int, value: Long, currencyCode: String) {
        if (index != 0 && index in 0 until conversionList.size) {
            disabledTopItemInput()
            val element = mutableList[index]
            with(mutableList) {
                remove(element)
                add(0, element)
            }
            currencyUseCase.setCurrencyCode(currencyCode)
            setInputValue(value, true)
            mutableUpdate.value = UpdateType.NewTopItem(index)
        }
    }

    fun setInputValue(newValue: Long, isNewTopItem: Boolean = false) {
        inputValue = newValue
        if (!isNewTopItem) {
            Log.i("--FROM INPUT AMOUNT--", "Calling update list. Value: $inputValue")
            updateStateList(currencyUseCase.currencyStates.value)
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
        private const val EUROPEAN_DEFAULT_AMOUNT = 1000L
    }
}
