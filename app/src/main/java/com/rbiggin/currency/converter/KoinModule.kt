package com.rbiggin.currency.converter

import com.rbiggin.currency.converter.feature.conversion.network.RetrofitConversionService
import com.rbiggin.currency.converter.feature.conversion.controller.ConversionNetworkApi
import com.rbiggin.currency.converter.feature.conversion.controller.ConversionService
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionController
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionDataSource
import com.rbiggin.currency.converter.feature.conversion.entity.ConversionRepository
import com.rbiggin.currency.converter.feature.metadata.controller.MetaDataService
import com.rbiggin.currency.converter.feature.metadata.controller.MetaDataNetworkApi
import com.rbiggin.currency.converter.feature.metadata.entity.MetaDataController
import com.rbiggin.currency.converter.feature.metadata.entity.MetaDataDataSource
import com.rbiggin.currency.converter.feature.metadata.entity.MetaDataRepository
import com.rbiggin.currency.converter.feature.metadata.network.RetrofitMetaDataService
import com.rbiggin.currency.converter.presentation.CurrencyViewModel
import com.rbiggin.currency.converter.usecase.CurrencyInteractor
import com.rbiggin.currency.converter.usecase.CurrencyUseCase
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {

    viewModel { CurrencyViewModel(get()) }

    factory<CurrencyUseCase> { CurrencyInteractor(get(), get()) }

    single<ConversionDataSource> {
        ConversionRepository(
            get()
        )
    }

    single<MetaDataDataSource> {
        MetaDataRepository(
            get()
        )
    }

    factory<ConversionController> {
        ConversionService(
            get()
        )
    }

    factory<MetaDataController> {
        MetaDataService(
            get()
        )
    }

    factory<ConversionNetworkApi> { RetrofitConversionService() }

    factory<MetaDataNetworkApi> { RetrofitMetaDataService() }
}