package com.example.binancews.data

import com.example.binancews.model.CryptoTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BinanceRepository(symbols: List<String>) {
    private val manager = WebSocketManager(symbols)
    private val _tickers = MutableStateFlow<Map<String, CryptoTicker>>(emptyMap())
    val tickers: StateFlow<Map<String, CryptoTicker>> = _tickers

    private val scope = CoroutineScope(Dispatchers.IO)

    fun start() {
        manager.connect()
        scope.launch {
            manager.events.collectLatest { ev ->
                when (ev) {
                    is WsEvent.Message -> {
                        try {
                            val json = Json.parseToJsonElement(ev.text).jsonObject
                            val s = json["s"]?.jsonPrimitive?.content ?: return@collectLatest
                            val price = json["c"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: return@collectLatest
                            val change = json["p"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                            val changePct = json["P"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                            val ticker = CryptoTicker(s, price, change, changePct)
                            val m = _tickers.value.toMutableMap()
                            m[s] = ticker
                            _tickers.value = m.toMap()
                        } catch (t: Throwable) {
                            // ignore parsing errors for robustness
                        }
                    }
                    is WsEvent.Open -> { /* handle open */ }
                    is WsEvent.Closed -> { /* handle closed */ }
                    is WsEvent.Failure -> {
                        // Could attempt reconnection logic here
                        manager.connect()
                    }
                }
            }
        }
    }

    fun stop() {
        manager.close()
    }
}
