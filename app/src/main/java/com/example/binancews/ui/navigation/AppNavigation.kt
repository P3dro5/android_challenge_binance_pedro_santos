package com.example.binancews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.binancews.ui.CryptoListComposeView
import com.example.binancews.ui.DetailScreenComposeView
import com.example.binancews.ui.viewmodel.CryptoViewModel

@Composable
fun AppNavigation() {
    val viewModel: CryptoViewModel = hiltViewModel()

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CryptoListComposeView(
                onClickCrypto = { crypto ->
                    navController.navigate("detail/${crypto.symbol}")
                }
            )
        }
        composable(
            route = "detail/{symbol}",
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: return@composable
            DetailScreenComposeView(symbol = symbol, viewModel = viewModel) {
                navController.popBackStack()
            }
        }
    }
}