package com.viktoriastoycheva.manicurear.network

import com.google.gson.GsonBuilder
import com.viktoriastoycheva.manicurear.adapters.LocalDateTimeAdapter
import com.viktoriastoycheva.manicurear.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

object ApiClient {
    // ВАЖНО: "10.0.2.2" е адресът на твоя компютър (localhost), видян от Android емулатора.
    // "localhost" в Android се отнася за самия телефон/емулатор, а не за IntelliJ.
    private const val BASE_URL = "http://127.0.0.1:8080/"

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Тази променлива ни позволява да пишем ApiClient.instance.getAllServices()
    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

/*object ApiClient {
    private const val BASE_URL = "http://localhost:8080/" // При тестване с реално устройство ще трябва да сменя това с IP-то на устройството
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}*/
