package com.example.binancews.model

import kotlinx.serialization.Serializable

@Serializable
data class SymbolInfoResponse(
    val symbols: List<SymbolInfo>
)

@Serializable
data class SymbolInfo(
    val symbol: String,
    val status: String,
    val quoteAsset: String
)