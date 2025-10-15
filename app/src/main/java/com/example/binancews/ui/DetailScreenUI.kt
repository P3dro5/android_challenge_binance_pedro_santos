package com.example.binancews.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.binancews.viewmodel.CryptoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenComposeView(
    symbol: String,
    viewModel: CryptoViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val tickerState by viewModel.tickerHandler.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(symbol.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if(tickerState is CryptoViewModel.TickerState.Idle) return@Box
            when(val state = tickerState) {
                is CryptoViewModel.TickerState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .wrapContentSize(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CryptoViewModel.TickerState.Failure -> {
                    ErrorMessageComposeView(state.message) { viewModel.startStreaming() }
                }
                CryptoViewModel.TickerState.NetworkError -> {
                    ErrorMessageComposeView("Network Error. Please check your connection and try again.") { viewModel.startStreaming() }
                }
                is CryptoViewModel.TickerState.Success -> {
                    val selectedTicker = state.tickers.find { it.symbol == symbol }
                    if(selectedTicker != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = selectedTicker.symbol,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Price: ${selectedTicker.price}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Change: ${selectedTicker.priceChange} (${selectedTicker.priceChange}%)",
                                color = if (selectedTicker.priceChange >= 0)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Volume: ${selectedTicker.volume}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                    }
                    }
                }
                else -> return@Box
            }
        }
    }
}