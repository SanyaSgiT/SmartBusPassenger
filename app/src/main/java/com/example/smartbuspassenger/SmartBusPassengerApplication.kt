package com.example.smartbuspassenger

import android.app.Application
import com.example.smartbuspassenger.di.dataModule
//import com.example.smartbuspassenger.di.dataModule
import com.example.smartbuspassenger.di.networkModule
import com.example.smartbuspassenger.di.presentationModule
import org.koin.android.ext.koin.androidContext
//import com.example.smartbuspassenger.di.presentationModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SmartBusPassengerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupDi()
    }

    private fun setupDi() {
        startKoin {
            androidLogger()
            androidContext(this@SmartBusPassengerApplication)
            modules(networkModule, dataModule, presentationModule)
        }
    }
}