package com.example.binancews

import app.cash.turbine.test
import com.example.binancews.data.WsEvent
import com.example.binancews.domain.GetSymbolsUseCase
import com.example.binancews.ui.viewmodel.CryptoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class CryptoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CryptoViewModel
    private lateinit var mockUseCase: GetSymbolsUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits Success on valid message`() = runTest {
        val jsonText = """{"s":"BTCUSDT","c":"30000.0","p":"100.0","P":"0.33","h":"31000.0","l":"29000.0","v":"1200.0"}"""
        whenever(mockUseCase()).thenReturn(flow {
            emit(WsEvent.Message(jsonText))
        })

        viewModel = CryptoViewModel(mockUseCase)

        viewModel.tickerHandler.test {
            val idle = awaitItem()
            assert(idle is CryptoViewModel.TickerState.Idle)

            val loading = awaitItem()
            assert(loading is CryptoViewModel.TickerState.Loading)

            // Success is emitted with parsed ticker
            val success = awaitItem()
            assert(success is CryptoViewModel.TickerState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Failure on invalid JSON`() = runTest {
        val invalidJson = """{"invalid":"data"}"""
        whenever(mockUseCase()).thenReturn(flow { emit(WsEvent.Message(invalidJson)) })

        viewModel = CryptoViewModel(mockUseCase)

        viewModel.tickerHandler.test {
            val idle = awaitItem()
            assert(idle is CryptoViewModel.TickerState.Idle)

            val loading = awaitItem()
            assert(loading is CryptoViewModel.TickerState.Loading)

            val failure = awaitItem()
            assert(failure is CryptoViewModel.TickerState.Failure)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits NetworkError on IOException`() = runTest {
        whenever(mockUseCase()).thenAnswer { throw IOException("No connection") }

        viewModel = CryptoViewModel(mockUseCase)

        viewModel.tickerHandler.test {
            val idle = awaitItem()
            assert(idle is CryptoViewModel.TickerState.Idle)

            val loading = awaitItem()
            assert(loading is CryptoViewModel.TickerState.Loading)

            val networkError = awaitItem()
            assert(networkError is CryptoViewModel.TickerState.NetworkError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Failure on generic Exception`() = runTest {
        whenever(mockUseCase()).thenAnswer { throw RuntimeException("Unexpected error") }

        viewModel = CryptoViewModel(mockUseCase)

        viewModel.tickerHandler.test {
            val idle = awaitItem()
            assert(idle is CryptoViewModel.TickerState.Idle)

            val loading = awaitItem()
            assert(loading is CryptoViewModel.TickerState.Loading)

            val failure = awaitItem()
            assert(failure is CryptoViewModel.TickerState.Failure)
            assert((failure as CryptoViewModel.TickerState.Failure).message == "Unexpected error")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits multiple Success messages`() = runTest {
        val msg1 = """{"s":"BTCUSDT","c":"30000.0","p":"100.0","P":"0.33","h":"31000.0","l":"29000.0","v":"1200.0"}"""
        val msg2 = """{"s":"ETHUSDT","c":"2000.0","p":"50.0","P":"2.5","h":"2100.0","l":"1900.0","v":"500.0"}"""

        whenever(mockUseCase()).thenReturn(flow {
            emit(WsEvent.Message(msg1))
            emit(WsEvent.Message(msg2))
        })

        viewModel = CryptoViewModel(mockUseCase)

        viewModel.tickerHandler.test {
            val idle = awaitItem()
            assert(idle is CryptoViewModel.TickerState.Idle)

            awaitItem() // Loading
            val first = awaitItem() // First ticker success
            assert((first as CryptoViewModel.TickerState.Success).tickers.size == 1)

            val second = awaitItem() // Second ticker success
            assert((second as CryptoViewModel.TickerState.Success).tickers.size == 2)

            cancelAndIgnoreRemainingEvents()
        }
    }
}