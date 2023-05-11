package com.example.smartbuspassenger

import android.app.Application
import com.example.smartbuspassenger.di.dataModule
//import com.example.smartbuspassenger.di.dataModule
import com.example.smartbuspassenger.di.networkModule
import com.example.smartbuspassenger.di.presentationModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
//import com.example.smartbuspassenger.di.presentationModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SmartBusPassengerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("6af80549-c660-4137-8516-d039ad43dc6e")
        MapKitFactory.initialize(this)
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