package com.example.binancews.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun BinanceWsAppTheme(content: @Composable ()->Unit) {
    val colors = darkColors()
    MaterialTheme(colors = colors, content = content)
}
