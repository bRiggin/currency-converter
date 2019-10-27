package com.rbiggin.currency.converter.usecase

import com.rbiggin.currency.converter.datasource.CurrencyConversionDataSource
import com.rbiggin.currency.converter.datasource.MetaDataDataSource
import com.rbiggin.currency.converter.model.CurrencyConversionEntity
import com.rbiggin.currency.converter.model.CurrencyMetaDataEntity
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.utils.TypedObservable
import com.rbiggin.currency.converter.utils.TypedObserver

class CurrencyInteractor(
    private val metaDataDataSource: MetaDataDataSource,
    private val conversionDataSource: CurrencyConversionDataSource
) : CurrencyUseCase {

    override val currencyStates: TypedObservable<Map<String, CurrencyState>> = TypedObservable()

    private val currentState: Map<String, CurrencyState>?
        get() = currencyStates.value

    private var conversionObserver = object : TypedObserver<Map<String, CurrencyConversionEntity>> {
        override fun onUpdate(value: Map<String, CurrencyConversionEntity>) {
            newFunciton(value)
        }
    }

//    private var metaDataObserver = object : TypedObserver<Map<String, CurrencyMetaDataEntity>> {
//        override fun onUpdate(value: Map<String, CurrencyMetaDataEntity>) {
//            updateStateFromMetaDataUpdate(value)
//        }
//    }

    init {
        conversionDataSource.observable.addTypedObserver(conversionObserver)
//        metaDataDataSource.observable.addTypedObserver(metaDataObserver)
    }

    override fun setCurrencyCode(code: String) {
        conversionDataSource.setCurrencyCode(code)
    }

    private fun newFunciton(entities: Map<String, CurrencyConversionEntity>) {
        metaDataDataSource.getMetaData(entities.mapKeysToSet())

        val newMap = mutableMapOf<String, CurrencyState>()

        entities.entries.forEach { entity ->
            val currencyCode = entity.key
            val conversionRate = entity.value.conversionRate
            val currencyName = metaDataDataSource.observable.value?.get(currencyCode)?.currencyName
            val flagUrl = metaDataDataSource.observable.value?.get(currencyCode)?.flagUrl
            val newEntity = CurrencyState(currencyCode, conversionRate, currencyName, flagUrl)
            newMap[currencyCode] =  newEntity
        }

        currencyStates.value = newMap
    }


    private fun updateStateFromMetaDataUpdate(entities: Map<String, CurrencyMetaDataEntity>) {
        val state = currentState?.toMutableMap() ?: mutableMapOf()

        val updateRequired = evaluateNewMetaDataEntitiesForUpdate(entities, state)

        if (updateRequired) updateState(state)
    }

    private fun evaluateNewMetaDataEntitiesForUpdate(
        entities: Map<String, CurrencyMetaDataEntity>,
        state: MutableMap<String, CurrencyState>
    ): Boolean {
        var updateRequired = false
        entities.entries.forEach { updateEntry ->
            state[updateEntry.key]?.let { currentState ->

                val flagUrl = if (currentState.flagAssetUrl != updateEntry.value.flagUrl) {
                    updateRequired = true
                    updateEntry.value.flagUrl
                } else {
                    currentState.flagAssetUrl
                }

                val currencyName = if (currentState.currencyName != updateEntry.value.currencyName) {
                    updateRequired = true
                    updateEntry.value.currencyName
                } else {
                    currentState.currencyName
                }

                state[updateEntry.key] = currentState.copy(currencyName = currencyName, flagAssetUrl = flagUrl)
            }
        }
        return updateRequired
    }

    private fun updateStateFromConversionsUpdate(entities: Map<String, CurrencyConversionEntity>) {
        val state = currentState?.toMutableMap() ?: mutableMapOf()

        metaDataDataSource.getMetaData(entities.mapKeysToSet())

        val updateRequired = evaluateNewConversionEntitiesForUpdate(entities, state)

        if (updateRequired) updateState(state)
    }

    private fun evaluateNewConversionEntitiesForUpdate(
        entities: Map<String, CurrencyConversionEntity>,
        state: MutableMap<String, CurrencyState>
    ): Boolean {
        var updateRequired = false
        entities.entries.forEach { updateEntry ->
            val currentEntry = state[updateEntry.key]

            if (currentEntry != null) {
                if (currentEntry.conversionRate != updateEntry.value.conversionRate) {
                    state[updateEntry.key] =
                        currentEntry.copy(conversionRate = updateEntry.value.conversionRate)
                    updateRequired = true
                }
            } else {
                state[updateEntry.key] =
                    CurrencyState(updateEntry.value.subjectCode, updateEntry.value.conversionRate)
                updateRequired = true
            }
        }
        return updateRequired
    }

    private fun updateState(state: Map<String, CurrencyState>) {
        currencyStates.value = state
    }

    private fun Map<String, *>.mapKeysToSet(): Set<String> {
        val set = mutableSetOf<String>()
        this.entries.forEach { set.add(it.key) }
        return set
    }
}
