package com.example.binancews.domain

import com.example.binancews.data.WsEvent
import com.example.binancews.domain.model.SymbolInfo
import kotlinx.coroutines.flow.Flow

interface BinanceRepository {
    suspend fun getUsdtSymbols(): List<SymbolInfo>
    suspend fun connectToTickerStream(symbols: List<String>): Flow<WsEvent>
    fun closeTickerStream()
}