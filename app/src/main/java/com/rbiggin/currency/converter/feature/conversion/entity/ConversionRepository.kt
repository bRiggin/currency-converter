package com.rbiggin.currency.converter.feature.conversion.entity

import com.rbiggin.currency.converter.model.ConversionDto
import com.rbiggin.currency.converter.model.ConversionEntity
import com.rbiggin.currency.converter.utils.TypedObservable

class ConversionRepository(
    private val controller: ConversionController,
    private val mapper: ConversionMapper = ConversionMapper
) : ConversionDataSource {

    override val observable: TypedObservable<Map<String, ConversionEntity>> =
        TypedObservable()

    private val currentState: Map<String, ConversionEntity>?
        get() = observable.value

    private val updateListener: (Set<ConversionDto>) -> Unit = { update ->
        handleApiUpdate(update)
    }

    init {
        controller.apply {
            setUpdateListener(updateListener)
            setCurrencyCode(DEFAULT_CURRENCY_CODE)
        }
    }

    override fun setCurrencyCode(code: String) {
        controller.setCurrencyCode(code)
    }

    private fun handleApiUpdate(update: Set<ConversionDto>) {
        var publishUpdateRequired = false
        val newState = currentState?.toMutableMap() ?: mutableMapOf()

        update.forEach { dto ->
            val newEntity = mapper.convertDtoToEntity(dto)
            if (currentState?.containsKey(newEntity.subjectCode) == true) {
                if (currentState?.get(newEntity.subjectCode) != newEntity) {
                    newState[newEntity.subjectCode] = newEntity
                    publishUpdateRequired = true
                }
            } else {
                newState[newEntity.subjectCode] = newEntity
                publishUpdateRequired = true
            }
        }

        if (publishUpdateRequired) {
            publishNewState(newState)
        }
    }

    private fun publishNewState(newState: Map<String, ConversionEntity>) {
        observable.value = newState
    }

    companion object {
        const val DEFAULT_CURRENCY_CODE = "EUR"
    }
}