package com.example.binancews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.binancews.data.WsEvent
import com.example.binancews.domain.GetSymbolsUseCase
import com.example.binancews.model.CryptoListingTicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val getSymbolsStreamUseCase: GetSymbolsUseCase
) : ViewModel() {
    private val tickerMap = mutableMapOf<String, CryptoListingTicker>()

    private val _tickerHandler = MutableStateFlow<TickerState>(TickerState.Idle)
    val tickerHandler = _tickerHandler.asStateFlow()

    init {
        startStreaming()
    }

    fun startStreaming() {
        viewModelScope.launch {
                _tickerHandler.emit(TickerState.Loading)
                try {
                    getSymbolsStreamUseCase().collect { event ->
                        when (event) {
                            is WsEvent.Message -> parseTicker(event.text)
                            is WsEvent.Open -> {
                                _tickerHandler.emit(TickerState.Loading)
                            }
                            is WsEvent.Closed -> Unit
                            is WsEvent.Failure -> {
                                val errorMessage = event.t?.message ?: "Error, please try again later"
                                _tickerHandler.emit(TickerState.Failure(errorMessage))
                            }
                        }
                    }
                } catch(e: IOException) {
                    _tickerHandler.emit(TickerState.NetworkError)
                } catch(e: Exception) {
                    _tickerHandler.emit(TickerState.Failure(message = e.message ?: "Error, please try again later."))
                }
        }
    }

    private suspend fun parseTicker(jsonText: String) {
        try {
            val json = Json.parseToJsonElement(jsonText).jsonObject
            val symbol = json["s"]?.jsonPrimitive?.content ?: return
            val price = json["c"]?.jsonPrimitive?.double ?: return
            val change = json["p"]?.jsonPrimitive?.double ?: 0.0
            val percent = json["P"]?.jsonPrimitive?.double ?: 0.0
            val high = json["h"]?.jsonPrimitive?.double ?: 0.0
            val low = json["l"]?.jsonPrimitive?.double ?: 0.0
            val volume = json["v"]?.jsonPrimitive?.double ?: 0.0
            val ticker = CryptoListingTicker(
                symbol = symbol,
                price = price,
                priceChange = change,
                priceChangePercent = percent,
                high = high,
                low = low,
                volume = volume
            )

            tickerMap[symbol] = ticker
            _tickerHandler.emit(TickerState.Success(tickerMap.values.toList()))
        } catch (e: Exception) {
            _tickerHandler.emit(TickerState.Failure(e.message ?: "Error, please try again later."))
        }
    }

    sealed class TickerState {
        data object Idle: TickerState()
        data object Loading: TickerState()
        data class Success(val tickers: List<CryptoListingTicker>): TickerState()
        data object NetworkError: TickerState()
        data class Failure(val message: String): TickerState()
    }
}