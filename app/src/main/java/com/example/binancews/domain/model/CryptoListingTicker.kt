package com.example.binancews.domain.model

data class CryptoListingTicker(
    val symbol: String,
    val price: Double,
    val priceChange: Double,
    val priceChangePercent: Double,
    val high: Double,
    val low: Double,
    val volume: Double
)