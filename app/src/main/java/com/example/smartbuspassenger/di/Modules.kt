package com.example.smartbuspassenger.di

//import com.example.bookcrossingapplication.data.api.BookApi
//import com.example.bookcrossingapplication.data.repository.PreferenceRepository
//import com.example.bookcrossingapplication.data.repository.UserRepository
//import com.example.bookcrossingapplication.ui.user.UserViewModel
import com.example.smartbuspassenger.data.api.TransportApi
import com.example.smartbuspassenger.data.api.UserApi
import com.example.smartbuspassenger.data.repository.RoutesRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    factory { RoutesRepository(get()) }
}

//val presentationModule = module {
//    viewModel { UserViewModel(get(), get()) }
//}

val networkModule = module {
    factory {
        val contentType = "application/json".toMediaTypeOrNull()!!

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        Retrofit.Builder()
            .baseUrl("http://37.194.210.121:4721/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    factory { get<Retrofit>().create(UserApi::class.java) }
    factory { get<Retrofit>().create(TransportApi::class.java) }
    //factory { get<Retrofit>().create(BookApi::class.java) }
}