package com.example.smartbuspassenger

import android.app.Application
import com.example.smartbuspassenger.di.dataModule
//import com.example.smartbuspassenger.di.dataModule
import com.example.smartbuspassenger.di.networkModule
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
            androidContext(this@SmartBusPassengerApplication) //TODO спросить у Сережи, что это такое и как это работает
            //modules(dataModule, presentationModule, networkModule)
            modules(networkModule, dataModule)
        }
    }
}