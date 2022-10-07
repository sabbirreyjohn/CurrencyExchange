package com.androidrey.currencyexchange.ui.view.converter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.androidrey.currencyexchange.R
import com.androidrey.currencyexchange.model.Account
import com.androidrey.currencyexchange.model.Rate
import com.androidrey.currencyexchange.util.roundOffDecimal
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ConverterScreen(navController: NavHostController, viewModel: ConverterViewModel) {

    val accountInfo by viewModel.account.collectAsState()
    val rates by viewModel.rates.collectAsState()
    val receiveAmount by viewModel.receiveAmount.collectAsState()
    val conversionMessage by viewModel.conversionMessage.collectAsState()
    val commissionFee by viewModel.commissionFee.collectAsState()
    val pagerState = rememberPagerState()

    val dialogState: MutableState<Boolean> = remember { mutableStateOf(false) }


    if (dialogState.value && conversionMessage.isNotEmpty()) {
        ShowConfirmationDialog(conversionMessage) { dialogState.value = false }
    }

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_balances),
            style = MaterialTheme.typography.overline
        )
        HorizontalPager(
            count = accountInfo.size,
            state = pagerState,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(end = 128.dp)
        ) {

            Card(
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${roundOffDecimal(accountInfo[it].balance)} ${accountInfo[it].currency}",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        CurrencyFields(
            accountInfo,
            rates,
            receiveAmount,
            onCalculate = { sellCurrency, receiveCurrency, sellAmount ->
                try {
                    val result = if (sellAmount.isEmpty()) 0.0 else sellAmount.toDouble()
                    viewModel.convertCurrency(sellCurrency, receiveCurrency, result)
                } catch (e: NumberFormatException) {

                }
            },
            onSubmit = { sellCurrency, sellAmount, receiveCurrency, receiveAmount ->

                viewModel.submitConvertedCurrency(
                    sellCurrency,
                    sellAmount,
                    receiveCurrency,
                    receiveAmount
                )

                dialogState.value = true

            }
        )

    }
}

@Composable
fun CurrencyFields(
    accountInfo: MutableList<Account>,
    rates: MutableList<Rate>,
    receiveAmount: Double,
    onCalculate: (sellCurrency: String, receiveCurrency: String, sellAmount: String) -> Unit,
    onSubmit: (sellCurrency: String, sellAmount: Double, receiveCurrency: String, receiveAmount: Double) -> Unit
) {

    val sellExpanded = remember { mutableStateOf(false) }
    val sellCurrencyValue =
        remember { mutableStateOf(if (accountInfo.size > 0) accountInfo[0].currency else "EUR") }

    val receiveExpanded = remember { mutableStateOf(false) }
    val receiveCurrencyValue =
        remember { mutableStateOf(if (rates.size > 0) rates[0].currency else "USD") }

    var sellAmount by remember { mutableStateOf(TextFieldValue("0.00")) }

    Text(
        text = stringResource(R.string.currency_exchange),
        style = MaterialTheme.typography.overline
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_up),
                contentDescription = "up",
                modifier = Modifier.weight(1f)
            )
            Text(text = stringResource(R.string.sell), modifier = Modifier.weight(1f))
            TextField(
                modifier = Modifier.weight(6f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = sellAmount,
                onValueChange = { newText ->
                    sellAmount = newText
                    onCalculate(
                        sellCurrencyValue.value,
                        receiveCurrencyValue.value,
                        sellAmount.text
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = stringResource(R.string.enter_amount), textAlign = TextAlign.End) },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
            )
            Row(modifier = Modifier
                .weight(2f)
                .clickable {
                    sellExpanded.value = !sellExpanded.value
                }) {
                Text(text = sellCurrencyValue.value)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                DropdownMenu(expanded = sellExpanded.value, onDismissRequest = {
                    sellExpanded.value = false
                }) {
                    accountInfo.forEach {
                        DropdownMenuItem(onClick = {
                            sellCurrencyValue.value = it.currency
                            sellExpanded.value = false
                            onCalculate(
                                sellCurrencyValue.value,
                                receiveCurrencyValue.value,
                                sellAmount.text
                            )
                        }) {
                            Text(text = it.currency)
                        }
                    }
                }
            }
        }

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_down),
                contentDescription = "down",
                modifier = Modifier.weight(1f)
            )
            Text(text = stringResource(R.string.receive), modifier = Modifier.weight(2f))
            Text(
                text = "+${receiveAmount}",
                modifier = Modifier.weight(5f),
                textAlign = TextAlign.End,
                color = Color.Green
            )
            Row(modifier = Modifier
                .weight(2f)
                .clickable {
                    receiveExpanded.value = !receiveExpanded.value
                }) {
                Text(text = receiveCurrencyValue.value)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                DropdownMenu(expanded = receiveExpanded.value, onDismissRequest = {
                    receiveExpanded.value = false
                }) {
                    rates.forEach {
                        DropdownMenuItem(onClick = {
                            receiveCurrencyValue.value = it.currency
                            receiveExpanded.value = false
                            onCalculate(
                                sellCurrencyValue.value,
                                receiveCurrencyValue.value,
                                sellAmount.text
                            )
                        }) { Text(text = it.currency) }
                    }
                }
            }
        }
        Divider()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (sellAmount.text.isNotEmpty()) {
                try {
                    val result = sellAmount.text.toDouble()
                    if (result > 0) {
                        onSubmit(
                            sellCurrencyValue.value,
                            result,
                            receiveCurrencyValue.value,
                            receiveAmount
                        )
                    }
                } catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.submit_button_text))
        }
    }

}

@Composable
fun ShowConfirmationDialog(conversionMessage: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() },
        content = {
            Surface(modifier = Modifier.background(Color.White)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = conversionMessage, textAlign = TextAlign.Center)
                    Button(onClick = { onDismiss() }) {
                        Text(text = "Ok")
                    }
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

