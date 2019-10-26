package com.rbiggin.currency.converter.view

interface CurrencyAdapterListener {
    fun onNewInputValue(input: Int)

    fun onItemClicked(index: Int)
}
