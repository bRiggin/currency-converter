package com.rbiggin.currency.converter.feature.metadata.entity

import com.rbiggin.currency.converter.model.MetaDataEntity
import com.rbiggin.currency.converter.model.MetaDataDto
import com.rbiggin.currency.converter.utils.TypedObservable

class MetaDataRepository(
    private val controller: MetaDataController,
    private val mapper: MetaDataMapper = MetaDataMapper
) : MetaDataDataSource {

    override val observable: TypedObservable<Map<String, MetaDataEntity>> =
        TypedObservable()

    private val currentState: Map<String, MetaDataEntity>?
        get() = observable.value

    private val updateListener: (MetaDataDto) -> Unit = { update ->
        handleApiUpdate(update)
    }

    private val errorListener: (String, Int?) -> Unit = { currencyCode, errorCode ->
        currencyPending = false
        currenciesQueue.remove(currencyCode)
        updateQueueAndQueryApi()
    }

    private val currenciesQueue = mutableSetOf<String>()
    private var currencyPending = false

    init {
        controller.apply {
            setUpdateListener(updateListener)
            setErrorListener(errorListener)
        }
    }

    override fun getMetaData(currencyCodes: Set<String>) {
        val requiredCurrencies = mutableSetOf<String>()
        currencyCodes.forEach {
            if (currentState?.containsKey(it) != true) requiredCurrencies.add(it)
        }

        if (requiredCurrencies.isNotEmpty())
            updateQueueAndQueryApi(requiredCurrencies)
    }

    private fun updateQueueAndQueryApi(currencies: Set<String>? = null) {
        currencies?.forEach {
            if (!currenciesQueue.contains(it))
                currenciesQueue.add(it)
        }

        if (!currencyPending && currenciesQueue.isNotEmpty()) requestCurrency(currenciesQueue.first())
    }

    private fun requestCurrency(code: String) {
        currencyPending = true
        currenciesQueue.remove(code)
        controller.getMetaData(code)
    }

    private fun handleApiUpdate(update: MetaDataDto) {
        currencyPending = false
        val newEntity = mapper.convertDtoToEntity(update)

        val newState = (currentState ?: mapOf()).toMutableMap()
        newState[newEntity.currencyCode] = newEntity

        publishNewState(newState)
        updateQueueAndQueryApi()
    }

    private fun publishNewState(newState: Map<String, MetaDataEntity>) {
        observable.value = newState
    }
}