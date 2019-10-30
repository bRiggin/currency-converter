package com.rbiggin.currency.converter.usecase

import com.rbiggin.currency.converter.feature.conversion.entity.ConversionDataSource
import com.rbiggin.currency.converter.feature.metadata.entity.MetaDataDataSource
import com.rbiggin.currency.converter.model.ConversionEntity
import com.rbiggin.currency.converter.model.MetaDataEntity
import com.rbiggin.currency.converter.model.CurrencyState
import com.rbiggin.currency.converter.utils.TypedObservable
import com.rbiggin.currency.converter.utils.TypedObserver

class CurrencyInteractor(
    private val metaDataDataSource: MetaDataDataSource,
    private val conversionDataSource: ConversionDataSource
) : CurrencyUseCase {

    override val currencyStates: TypedObservable<Map<String, CurrencyState>> = TypedObservable()

    private val metaDataState: Map<String, MetaDataEntity>?
        get() = metaDataDataSource.observable.value

    private var conversionObserver = object : TypedObserver<Map<String, ConversionEntity>> {
        override fun onUpdate(value: Map<String, ConversionEntity>) {
            updateCurrencyStates(value)
        }
    }

    private var metaDataObserver = object : TypedObserver<Map<String, MetaDataEntity>> {
        override fun onUpdate(value: Map<String, MetaDataEntity>) {
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

    private fun updateCurrencyStates(entities: Map<String, ConversionEntity>?) {
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
