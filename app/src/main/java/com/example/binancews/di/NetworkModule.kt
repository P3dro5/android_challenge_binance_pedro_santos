package com.example.binancews.di

import com.example.binancews.data.BinanceApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit

class NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    fun provideBinanceApi(): BinanceApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.binance.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(BinanceApi::class.java)
    }
}