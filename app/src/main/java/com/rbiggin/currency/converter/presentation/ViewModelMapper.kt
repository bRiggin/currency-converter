package com.rbiggin.currency.converter.presentation

import com.rbiggin.currency.converter.model.CurrencyModel
import com.rbiggin.currency.converter.model.CurrencyState

object ViewModelMapper {

    fun stateToModel(amount: Double, state: CurrencyState): CurrencyModel {
        val value = state.subjectCurrencyToTarget(amount)
        return CurrencyModel(state.currencyCode, state.conversionRate, value, state.currencyName, state.flagAssetUrl)
    }

    fun stateAndModelEquivalent(
        state: CurrencyState,
        model: CurrencyModel
    ): Boolean = state.conversionRate == model.conversionRate &&
                state.currencyCode == model.currencyCode &&
                state.currencyName == model.currencyName &&
                state.flagAssetUrl == model.flagAssetUrl
}
