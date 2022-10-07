package com.androidrey.currencyexchange.ui.view.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidrey.currencyexchange.ui.theme.CurrencyExchangeTheme
import com.androidrey.currencyexchange.ui.view.converter.ConverterScreen
import com.androidrey.currencyexchange.ui.view.converter.ConverterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyExchangeTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    Column {
        TopAppBar(
            elevation = 2.dp,
            title = { Text(text = "Currency converter") }
        )
        val navController = rememberNavController()
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "Converter") {
                composable("Converter") {
                    val viewModel = hiltViewModel<ConverterViewModel>()
                    ConverterScreen(navController, viewModel)
                }
            }
        }
    }
}


