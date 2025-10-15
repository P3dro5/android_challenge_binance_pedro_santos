package com.example.binancews.data

import com.example.binancews.model.SymbolInfoResponse
import retrofit2.http.GET

interface BinanceService {
    @GET("api/v3/exchangeInfo")
    suspend fun getSymbolsExchangeInfo(): SymbolInfoResponse
}