package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.api.CurrencyConversionApi
import com.rbiggin.currency.converter.model.CurrencyDto
import com.rbiggin.currency.converter.model.CurrencyEntity
import com.rbiggin.currency.converter.utils.TypedObservable

class CurrencyConverterRepository(
    private val api: CurrencyConversionApi,
    private val mapper: CurrencyConversionMapper = CurrencyConversionMapper
) : CurrencyConverterDataSource {

    override val observable: TypedObservable<Map<String, CurrencyEntity>> =
        TypedObservable()

    private val currentState: Map<String, CurrencyEntity>?
        get() = observable.value

    private val updateListener: (Set<CurrencyDto>) -> Unit = { update ->
        handleApiUpdate(update)
    }

    init {
        api.apply {
            setUpdateListener(updateListener)
            setCurrencyCode(DEFAULT_CURRENCY_CODE)
        }
    }

    override fun setCurrencyCode(code: String) {
        api.setCurrencyCode(code)
    }

    private fun handleApiUpdate(update: Set<CurrencyDto>) {
        var publishUpdateRequired = false
        val newState = currentState?.toMutableMap() ?: mutableMapOf()

        update.forEach { dto ->
            val newEntity = mapper.convertDtoToEntity(dto)
            if (currentState?.containsKey(newEntity.nativeCode) == true) {
                if (currentState?.get(newEntity.nativeCode) != newEntity) {
                    newState[newEntity.nativeCode] = newEntity
                    publishUpdateRequired = true
                }
            } else {
                newState[newEntity.nativeCode] = newEntity
                publishUpdateRequired = true
            }
        }

        if (publishUpdateRequired) {
            publishNewState(newState)
        }
    }

    private fun publishNewState(newState: Map<String, CurrencyEntity>) {
        observable.value = newState
    }

    companion object {
        const val DEFAULT_CURRENCY_CODE = "EUR"
    }
}