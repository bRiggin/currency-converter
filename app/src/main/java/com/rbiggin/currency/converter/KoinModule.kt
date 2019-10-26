package com.rbiggin.currency.converter

import com.rbiggin.currency.converter.api.*
import com.rbiggin.currency.converter.datasource.CurrencyConversionDataSource
import com.rbiggin.currency.converter.datasource.CurrencyConversionRepository
import com.rbiggin.currency.converter.datasource.MetaDataDataSource
import com.rbiggin.currency.converter.datasource.MetaDataRepository
import com.rbiggin.currency.converter.network.RetroFitApi
import com.rbiggin.currency.converter.presentation.CurrencyConversionViewModel
import com.rbiggin.currency.converter.usecase.CurrencyInteractor
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {

    viewModel { CurrencyConversionViewModel(get()) }

    factory<CurrencyUseCase> { CurrencyInteractor(get(), get()) }

    single<CurrencyConversionDataSource> { CurrencyConversionRepository(get()) }

    single<MetaDataDataSource> { MetaDataRepository(get()) }

    factory<CurrencyConversionApi> { CurrencyConverterService(get()) }

    factory<MetaDataApi> { CurrencyMetaDataService(get()) }

    factory<CurrencyNetworkApi> { RetroFitApi() }

    factory<MetaDataNetworkApi> { RetroFitMetaDataApi() }
}