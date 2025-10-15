package com.example.binancews.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.binancews.model.CryptoListingTicker
import com.example.binancews.viewmodel.CryptoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListComposeView(
    viewModel: CryptoViewModel = hiltViewModel(),
    onClickCrypto: (CryptoListingTicker) -> Unit
) {
    val tickerState by viewModel.tickerHandler.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar({})
        }
    ) { paddingValues ->
        if(tickerState is CryptoViewModel.TickerState.Idle) return@Scaffold
        when (val state = tickerState) {
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
                CryptoList(
                    items = state.tickers,
                    modifier = Modifier.padding(paddingValues),
                    onClickCrypto = onClickCrypto
                )
            }
            else -> return@Scaffold
        }
    }
}

@Composable
fun CryptoList(items: List<CryptoListingTicker>, modifier: Modifier, onClickCrypto: (CryptoListingTicker) -> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(top = 50.dp, start = 8.dp, end = 8.dp)) {
        items(items) {
            CryptoRow(it, onClickCrypto)
        }
    }
}

@Composable
fun CryptoRow(t: CryptoListingTicker, onClickCrypto: (CryptoListingTicker)->Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(6.dp).clickable { onClickCrypto(t) }) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(t.symbol, style = MaterialTheme.typography.bodyLarge)
                Text("Price: ${'$'}${"%.4f".format(t.price)}", style = MaterialTheme.typography.bodyMedium)
            }
            Column {
                Text(if (t.priceChange >= 0) "+${"%.4f".format(t.priceChange)}" else "%.4f".format(t.priceChange))
                Text(if (t.priceChangePercent >= 0) "+${"%.2f".format(t.priceChangePercent)}%" else "${"%.2f".format(t.priceChangePercent)}%" )
            }
        }
    }
}
