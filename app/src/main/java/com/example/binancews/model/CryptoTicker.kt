package com.example.binancews.model

data class CryptoTicker(
    val symbol: String,
    val price: Double,
    val priceChange: Double,
    val priceChangePercent: Double
)
