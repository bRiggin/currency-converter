package com.rbiggin.currency.converter

import com.rbiggin.currency.converter.api.CurrencyConversionApi
import com.rbiggin.currency.converter.api.CurrencyConverterService
import com.rbiggin.currency.converter.api.CurrencyNetworkApi
import com.rbiggin.currency.converter.datasource.CurrencyConversionDataSource
import com.rbiggin.currency.converter.datasource.CurrencyConversionRepository
import com.rbiggin.currency.converter.network.RetroFitApi
import com.rbiggin.currency.converter.presentation.CurrencyConversionViewModel
import com.rbiggin.currency.converter.usecase.CurrencyInteractor
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {

    viewModel { CurrencyConversionViewModel(get()) }

    factory<CurrencyUseCase> { CurrencyInteractor(get()) }

    single<CurrencyConversionDataSource> { CurrencyConversionRepository(get()) }

    factory<CurrencyConversionApi> { CurrencyConverterService(get()) }

    factory<CurrencyNetworkApi> { RetroFitApi() }
}