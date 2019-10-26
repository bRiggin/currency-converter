package com.rbiggin.currency.converter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rbiggin.currency.converter.model.CurrencyModel
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import com.rbiggin.currency.converter.utils.TypedObserver

class CurrencyConversionViewModel(
    currencyUseCase: CurrencyUseCase,
    private val mapper: ViewModelMapper = ViewModelMapper
) : ViewModel() {

    var inputValue: Int = 100
        set(_) = handleNewInputValue()

    private val mutableUpdate = MutableLiveData<UpdateType>()
    val listUpdates: LiveData<UpdateType>
        get() = mutableUpdate

    private var mutableList: MutableList<CurrencyModel> = mutableListOf()
    val conversionList: List<CurrencyModel>
        get() = mutableList

    private val observer = object : TypedObserver<Map<String, CurrencyState>> {
        override fun onUpdate(value: Map<String, CurrencyState>) {
            handleConversionUpdate(value)
        }
    }

    init {
        currencyUseCase.currencyStates.addTypedObserver(observer)
    }

    fun onItemTouched(index: Int) {
        if (index != 0 && index in 0 until conversionList.size) {
            val element = mutableList[index]
            with(mutableList) {
                remove(element)
                add(0, element)
            }
            mutableUpdate.value = UpdateType.NewTopItem(index)
        }
    }

    private fun handleNewInputValue() {
        val valueInEuros = mutableList[0].subjectCurrencyToTarget(inputValue.toDouble())
        val updatedIndexes = mutableListOf<Int>()
        val updatedItems = mutableMapOf<Int, CurrencyModel>()

        mutableList.forEachIndexed { index, currencyModel ->
            if (index != 0) {
                val newValue = currencyModel.targetToSubjectCurrency(valueInEuros.toDouble())
                val newModel = currencyModel.copy(value = newValue)
                updatedItems[index] = newModel
                updatedIndexes.add(index)
            }
        }
        updateList(updatedItems)
        publishItemsUpdate(updatedIndexes)
    }

    private fun updateList(itemsToUpdate: Map<Int, CurrencyModel>) {
        itemsToUpdate.entries.forEach {
            with(mutableList) {
                removeAt(it.key)
                add(it.key, it.value)
            }
        }
    }

    private fun handleConversionUpdate(update: Map<String, CurrencyState>) {
        if (mutableList.isEmpty()) {
            initialiseList(update)
        } else {
            updateListFromConversionUpdate(update)
        }
    }

    private fun initialiseList(update: Map<String, CurrencyState>) {
        val list = mutableListOf<CurrencyModel>()

        update.entries.forEach {
            val model = mapper.stateToModel(inputValue, it.value)
            if (it.key == EUROPEAN_CURRENCY_CODE) {
                list.add(0, model)
            } else {
                list.add(model)
            }
        }

        mutableList = list
        mutableUpdate.value = UpdateType.InitialUpdate
    }

    private fun updateListFromConversionUpdate(update: Map<String, CurrencyState>) {
        val updatedIndexes = mutableListOf<Int>()
        var numberOfNewItems = 0

        update.entries.forEach { updateEntry ->
            val currentElement = mutableList.find { it.currencyCode == updateEntry.key }
            val newElement = mapper.stateToModel(inputValue, updateEntry.value)

            if (currentElement != null && currentElement.value != newElement.value) {
                val index = mutableList.indexOf(currentElement)
                mutableList[index] = newElement
                updatedIndexes.add(index)
            } else if (currentElement == null) {
                mutableList.add(newElement)
                numberOfNewItems++
            }
        }

        publishItemsUpdate(updatedIndexes, numberOfNewItems)
    }

    private fun publishItemsUpdate(
        updatedIndexes: MutableList<Int>,
        numberOfNewItems: Int = 0
    ) {
        val newItemUpdate = when {
            numberOfNewItems != 0 -> NewItems(mutableList.size - numberOfNewItems, numberOfNewItems)
            else -> null
        }

        val update = UpdateType.ItemsUpdate(updatedIndexes, newItemUpdate)

        if (update.indexesChanged.isNotEmpty() || update.newItems != null) {
            mutableUpdate.value = update
        }
    }

    sealed class UpdateType {
        object InitialUpdate : UpdateType()
        data class ItemsUpdate(
            val indexesChanged: List<Int>,
            val newItems: NewItems? = null
        ) : UpdateType()

        data class NewTopItem(val fromIndex: Int) : UpdateType()
    }

    data class NewItems(val insertIndex: Int, val numberOfItems: Int)

    companion object {
        private const val EUROPEAN_CURRENCY_CODE = "EUR"
    }
}
