package com.rbiggin.currency.converter

import android.app.Application
import com.rbiggin.currency.converter.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyApplication: Application() {

    override fun onCreate(){
        super.onCreate()
        startKoin {
            androidContext(this@CurrencyApplication)
            modules(koinModule)
        }
    }
}