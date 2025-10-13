package com.example.binancews.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.binancews.model.CryptoTicker
import com.example.binancews.viewmodel.CryptoViewModel

@Composable
fun AppContent(viewModel: CryptoViewModel) {
    val items = viewModel.items.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar({})
        }
    ) { paddingValues ->
        CryptoList(
            items = items.value,
            modifier = Modifier.padding(paddingValues),
            onClick = { /* no-op */ }
        )
    }
}

@Composable
fun CryptoList(items: List<CryptoTicker>,modifier: Modifier, onClick: (CryptoTicker) -> Unit) {

    LazyColumn(modifier = modifier.fillMaxSize().padding(top = 50.dp, start = 8.dp, end = 8.dp)) {
        items(items) {
            CryptoRow(it, onClick)
        }
    }
}

@Composable
fun CryptoRow(t: CryptoTicker, onClick: (CryptoTicker)->Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(6.dp).clickable { onClick(t) }) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(t.symbol, style = MaterialTheme.typography.h6)
                Text("Price: ${'$'}${"%.4f".format(t.price)}", style = MaterialTheme.typography.body1)
            }
            Column {
                Text(if (t.priceChange >= 0) "+${"%.4f".format(t.priceChange)}" else "%.4f".format(t.priceChange))
                Text(if (t.priceChangePercent >= 0) "+${"%.2f".format(t.priceChangePercent)}%" else "${"%.2f".format(t.priceChangePercent)}%" )
            }
        }
    }
}
