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
    }
    private fun Map<String, *>.mapKeysToSet(): Set<String> {
        val set = mutableSetOf<String>()
        this.entries.forEach { set.add(it.key) }
        return set
    }
}
