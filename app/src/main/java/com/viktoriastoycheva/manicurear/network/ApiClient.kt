package com.viktoriastoycheva.manicurear.network

import com.google.gson.GsonBuilder
import com.viktoriastoycheva.manicurear.adapters.LocalDateTimeAdapter
import com.viktoriastoycheva.manicurear.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"//"http://127.0.0.1:8080/"

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
