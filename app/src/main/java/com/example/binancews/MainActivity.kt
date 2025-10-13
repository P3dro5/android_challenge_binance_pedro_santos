package com.example.binancews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.binancews.ui.AppContent
import com.example.binancews.ui.theme.BinanceWsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BinanceWsAppTheme {
                val vm: com.example.binancews.viewmodel.CryptoViewModel = viewModel()
                AppContent(viewModel = vm)
            }
        }
    }
}
