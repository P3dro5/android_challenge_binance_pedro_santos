package com.example.binancews.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.binancews.ui.navigation.AppNavigation
import com.example.binancews.ui.theme.BinanceWsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BinanceWsAppTheme {
                AppNavigation()
            }
        }
    }
}
