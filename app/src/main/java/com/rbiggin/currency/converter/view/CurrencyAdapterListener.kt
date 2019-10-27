package com.rbiggin.currency.converter.view

interface CurrencyAdapterListener {
    fun onNewInputValue(input: Long)

    fun onItemClicked(index: Int, currentValue: Long?, currencyCode: String?)
}
