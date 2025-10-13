package com.example.binancews.data

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

sealed class WsEvent {
    data class Message(val text: String): WsEvent()
    data object Open: WsEvent()
    data object Closed: WsEvent()
    data class Failure(val t: Throwable?): WsEvent()
}

class WebSocketManager(private val symbols: List<String>) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var ws: WebSocket? = null
    private val _events = Channel<WsEvent>(Channel.BUFFERED)
    val events: Flow<WsEvent> get() = _events.receiveAsFlow()

    fun connect() {
        val path = symbols.joinToString("/") { it.lowercase() + "@ticker" }
        val url = "wss://stream.binance.com:9443/ws/$path"
        val req = Request.Builder().url(url).build()
        ws = client.newWebSocket(req, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                _events.trySend(WsEvent.Open)
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                _events.trySend(WsEvent.Message(text))
            }
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                _events.trySend(WsEvent.Message(bytes.utf8()))
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                _events.trySend(WsEvent.Failure(t))
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                _events.trySend(WsEvent.Closed)
            }
        })
    }

    fun close() {
        ws?.close(1000, "Client closing")
        ws = null
    }
}
