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

    private val metaDataState: Map<String, CurrencyMetaDataEntity>?
        get() = metaDataDataSource.observable.value

    private var conversionObserver = object : TypedObserver<Map<String, CurrencyConversionEntity>> {
        override fun onUpdate(value: Map<String, CurrencyConversionEntity>) {
            updateCurrencyStates(value)
        }
    }

    private var metaDataObserver = object : TypedObserver<Map<String, CurrencyMetaDataEntity>> {
        override fun onUpdate(value: Map<String, CurrencyMetaDataEntity>) {
            updateCurrencyStates(conversionDataSource.observable.value)
        }
    }

    init {
        conversionDataSource.observable.addTypedObserver(conversionObserver)
        metaDataDataSource.observable.addTypedObserver(metaDataObserver)
    }

    override fun setCurrencyCode(code: String) {
        conversionDataSource.setCurrencyCode(code)
    }

    private fun updateCurrencyStates(entities: Map<String, CurrencyConversionEntity>?) {
        entities?.let {
            metaDataDataSource.getMetaData(entities.mapKeysToSet())

            val newMap = currencyStates.value?.toMutableMap() ?: mutableMapOf()

            entities.entries.forEach { entity ->
                val newEntity = CurrencyState(
                    entity.key,
                    entity.value.conversionRate,
                    metaDataState?.get(entity.key)?.currencyName,
                    metaDataState?.get(entity.key)?.flagUrl
                )
                newMap[entity.key] = newEntity
            }

            if (newMap.isNotEmpty())
                currencyStates.value = newMap
        }
    }

    private fun Map<String, *>.mapKeysToSet(): Set<String> {
        val set = mutableSetOf<String>()
        this.entries.forEach { set.add(it.key) }
        return set
    }
}
