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
    conversionDataSource: CurrencyConversionDataSource
) : CurrencyUseCase {

    override val currencyStates: TypedObservable<Map<String, CurrencyState>> = TypedObservable()

    private val currentState: Map<String, CurrencyState>?
        get() = currencyStates.value

    private var conversionObserver = object : TypedObserver<Map<String, CurrencyConversionEntity>> {
        override fun onUpdate(value: Map<String, CurrencyConversionEntity>) {
            updateStateFromConversionsUpdate(value)
        }
    }

    private var metaDataObserver = object : TypedObserver<Map<String, CurrencyMetaDataEntity>> {
        override fun onUpdate(value: Map<String, CurrencyMetaDataEntity>) {
            updateStateFromMetaDataUpdate(value)
        }
    }

    init {
        conversionDataSource.observable.addTypedObserver(conversionObserver)
        metaDataDataSource.observable.addTypedObserver(metaDataObserver)
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

                val currencyCode = if (currentState.currencyName != updateEntry.value.currencyName) {
                    updateRequired = true
                    updateEntry.value.currencyCode
                } else {
                    currentState.currencyCode
                }

                state[updateEntry.key] = currentState.copy(currencyName = currencyCode, flagAssetUrl = flagUrl)
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
