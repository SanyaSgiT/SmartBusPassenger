package com.example.smartbuspassenger.di

//import com.example.bookcrossingapplication.data.api.BookApi
//import com.example.bookcrossingapplication.data.repository.PreferenceRepository
//import com.example.bookcrossingapplication.data.repository.UserRepository
//import com.example.bookcrossingapplication.ui.user.UserViewModel
import com.example.smartbuspassenger.data.api.UserApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//val dataModule = module {
//    factory { PreferenceRepository(androidApplication().getSharedPreferences("prefs", Context.MODE_PRIVATE)) }
//    factory { UserRepository(get()) }
//}

//val presentationModule = module {
//    viewModel { UserViewModel(get(), get()) }
//}

val networkModule = module {
    factory {
        val contentType = "application/json".toMediaTypeOrNull()!!

        Retrofit.Builder()
            .baseUrl("http://37.194.210.121:4721/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    factory { get<Retrofit>().create(UserApi::class.java) }
    //factory { get<Retrofit>().create(BookApi::class.java) }
}

val interceptor = HttpLoggingInterceptor()

val client = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("http://37.194.210.121:4721/").client(client)
    .addConverterFactory(GsonConverterFactory.create()).build()

val userApi = retrofit.create(UserApi::class.java)