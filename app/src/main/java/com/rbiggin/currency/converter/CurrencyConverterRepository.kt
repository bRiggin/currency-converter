package com.rbiggin.currency.converter

class CurrencyConverterRepository(
    service: CurrencyConversionApi,
    private val mapper: CurrencyConversionMapper = CurrencyConversionMapper
) : CurrencyConverterDataSource {

    override val observable: TypedObservable<Map<String, CurrencyEntity>> = TypedObservable()

    private val currentState: Map<String, CurrencyEntity>?
        get() = observable.value

    private val updateListener: (Set<CurrencyDto>) -> Unit = { update ->
        handleApiUpdate(update)
    }

    init {
        service.apply {
            setOnUpdateListener(updateListener)
            setCurrencyCode(DEFAULT_CURRENCY_CODE)
        }
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