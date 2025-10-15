package com.example.binancews.data

import com.example.binancews.domain.BinanceRepository
import com.example.binancews.domain.model.SymbolInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinanceRepositoryImpl @Inject constructor(
    private val api: BinanceService
) : BinanceRepository {

    private var wsManager: WebSocketManager? = null

    override suspend fun getUsdtSymbols(): List<SymbolInfo> {
        val res = api.getSymbolsExchangeInfo()
        return res.symbols
            .filter { it.quoteAsset == "USDT" && it.status == "TRADING" }
            .take(5)
            .map { SymbolInfo(it.symbol, it.status, it.quoteAsset) }
    }

    override suspend fun connectToTickerStream(symbols: List<String>): Flow<WsEvent> {
        wsManager = WebSocketManager(symbols)
        wsManager?.connect()
        return wsManager!!.events
    }

    override fun closeTickerStream() {
        wsManager?.close()
    }
}