package com.rbiggin.currency.converter.usecase

import com.rbiggin.currency.converter.datasource.CurrencyConversionDataSource
import com.rbiggin.currency.converter.model.CurrencyConversionEntity
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.utils.TypedObservable
import com.rbiggin.currency.converter.utils.TypedObserver

class CurrencyInteractor(
    dataSource: CurrencyConversionDataSource
) : CurrencyUseCase {

    override val currencyStates: TypedObservable<Map<String, CurrencyState>> = TypedObservable()

    private val currentState: Map<String, CurrencyState>?
        get() = currencyStates.value

    private var observer = object : TypedObserver<Map<String, CurrencyConversionEntity>> {
        override fun onUpdate(value: Map<String, CurrencyConversionEntity>) {
            updateStateFromConversionsUpdate(value)
        }
    }

    init {
        dataSource.observable.addTypedObserver(observer)
    }

    private fun updateStateFromConversionsUpdate(entities: Map<String, CurrencyConversionEntity>) {
        val state = currentState?.toMutableMap() ?: mutableMapOf()

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
}
