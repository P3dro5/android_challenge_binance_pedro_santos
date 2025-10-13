package com.example.binancews.model

import kotlinx.serialization.Serializable

data class CryptoTicker(
    val symbol: String,
    val price: Double,
    val priceChange: Double,
    val priceChangePercent: Double
)
