package com.example.binancews.domain

import com.example.binancews.data.WsEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSymbolsUseCase @Inject constructor(
    private val repository: BinanceRepository
) {
    suspend operator fun invoke(selectedSymbol: String = ""): Flow<WsEvent> {
        if(selectedSymbol.isNotEmpty()) {
            val symbol = repository.getUsdtSymbols().filter { it.symbol == selectedSymbol }.map { it.symbol }
            return repository.connectToTickerStream(symbol)
        }
        else {
            val symbols = repository.getUsdtSymbols().map { it.symbol }
            return repository.connectToTickerStream(symbols)
        }
    }
}