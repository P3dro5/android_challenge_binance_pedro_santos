package com.example.binancews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.binancews.data.BinanceRepository
import com.example.binancews.model.CryptoTicker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoViewModel : ViewModel() {
    // default 6 symbols
    private val symbols = listOf("btcusdt","ethusdt","bnbusdt","adausdt","solusdt", "bnbbtc")
    private val repo = BinanceRepository(symbols)
    private val _items = MutableStateFlow<List<CryptoTicker>>(emptyList())
    val items: StateFlow<List<CryptoTicker>> = _items.asStateFlow()

    init {
        repo.start()
        viewModelScope.launch {
            repo.tickers.collect { map ->
                _items.value = symbols.map { sym ->
                    map[sym.uppercase()] ?: map[sym] ?: map[sym.lowercase()] ?:
                    CryptoTicker(sym.uppercase(), 0.0,0.0,0.0)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repo.stop()
    }
}
