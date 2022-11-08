package com.androidrey.currencyexchange.ui.view.converter

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.androidrey.currencyexchange.R
import com.androidrey.currencyexchange.di.StringResourceProvider
import com.androidrey.currencyexchange.model.Account
import com.androidrey.currencyexchange.model.Conversion
import com.androidrey.currencyexchange.model.Rate
import com.androidrey.currencyexchange.repository.RatesRepository
import com.androidrey.currencyexchange.util.COMMISSION_FEE_PERCENTAGE
import com.androidrey.currencyexchange.util.MAX_FREE_CONVERSION
import com.androidrey.currencyexchange.util.MAX_FREE_CONVERSION_AMOUNT
import com.androidrey.currencyexchange.util.Status
import com.androidrey.currencyexchange.workmanager.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("InvalidPeriodicWorkRequestInterval")
@HiltViewModel
class ConverterViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: RatesRepository,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {


    private val _rates = MutableStateFlow<MutableList<Rate>>(mutableListOf())
    val rates get() = _rates

    private val _account = MutableStateFlow<MutableList<Account>>(mutableListOf())
    val account get() = _account

    private val _receiveAmount = MutableStateFlow<Double>(0.00)
    val receiveAmount get() = _receiveAmount

    private val _sellAmount = MutableStateFlow<Double>(0.00)
    val sellAmount get() = _sellAmount

    private val _conversionMessage = MutableStateFlow("")
    val conversionMessage get() = _conversionMessage

    private val _commissionFee = MutableStateFlow(0.0)
    val commissionFee get() = _commissionFee


    init {
        loadRates()
        loadAccountInfo()
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(SyncWorker::class.java, 30, TimeUnit.MINUTES)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setConstraints(myConstraints)
            .addTag("myWorkManager")
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "myWorkManager",
            ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )


    }

    fun loadRates() {
        viewModelScope.launch {
            when (val status = repo.getRateResponseFromServer()) {
                is Status.Success -> {
                    val ratesList: MutableList<Rate> = mutableListOf()
                    status.data?.rates?.let {
                        it.forEach { entry ->
                            ratesList.add(
                                Rate(
                                    entry.key,
                                    entry.value
                                )
                            )
                        }
                        repo.insertRatesToLocalDB(ratesList)
                        loadRatesFromDb()
                    }
                }
                is Status.Error -> {
                    loadRatesFromDb()
                }
            }
        }
    }

    suspend fun loadRatesFromDb() {
        when (val dbStatus = repo.getRates()) {
            is Status.Success -> {
                dbStatus.data?.let {
                    _rates.value = it
                }
            }
            is Status.Error -> {
                _rates.value = mutableListOf()
            }
        }
    }

    private fun loadAccountInfo() {
        viewModelScope.launch {
            when (val dbStatus = repo.getAccountInfo()) {
                is Status.Success -> {
                    dbStatus.data?.let {
                        _account.value = it
                    }
                }
                is Status.Error -> {
                    _account.value = mutableListOf()
                }
            }
        }
    }

    fun convertCurrency(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            try {
                val fromRate = repo.getRate(from)
                val toRate = repo.getRate(to)
                _receiveAmount.value = (toRate / fromRate) * amount
            } catch (ex: Exception) {
                ex.printStackTrace()
                _receiveAmount.value = 0.0
            }
        }
    }

    fun submitConvertedCurrency(
        sellCurrency: String,
        sellAmount: Double,
        receiveCurrency: String,
        receiveAmount: Double,
    ) {
        viewModelScope.launch {
            if (sellCurrency.contentEquals(receiveCurrency)) {
                _conversionMessage.value =
                    stringResourceProvider.getString(R.string.select_different_currencies)
                return@launch
            }
            if (receiveAmount == 0.0) {
                _conversionMessage.value =
                    stringResourceProvider.getString(R.string.failed_to_load_rate)
                return@launch
            }
            _sellAmount.value = repo.getAccountBalance(sellCurrency) - sellAmount
            _commissionFee.value = 0.0
            if (repo.getNumberOfConversion() >= MAX_FREE_CONVERSION || sellAmount >= MAX_FREE_CONVERSION_AMOUNT) {
                _commissionFee.value = COMMISSION_FEE_PERCENTAGE * sellAmount / 100
                _sellAmount.value -= _commissionFee.value
            }
            val newReceiveAmount =
                if (repo.getAccountBalance(receiveCurrency) == null) 0.0 + receiveAmount
                else repo.getAccountBalance(receiveCurrency) + receiveAmount

            if (_sellAmount.value >= 0) {
                repo.insertAccountInfoToLocalDB(Account(sellCurrency, _sellAmount.value))
                repo.insertAccountInfoToLocalDB(
                    Account(
                        receiveCurrency,
                        newReceiveAmount
                    )
                )
                repo.insertConversionToLocalDB(
                    Conversion(
                        sellCurrency = sellCurrency,
                        receiveCurrency = receiveCurrency,
                        sellAmount = _sellAmount.value,
                        receiveAmount = newReceiveAmount
                    )
                )
                val commissionFeeText =
                    if (_commissionFee.value > 0) stringResourceProvider.getString(
                        R.string.commission_fee,
                        _commissionFee.value,
                        sellCurrency
                    ) else ""
                _conversionMessage.value =
                    stringResourceProvider.getString(
                        R.string.conversion_success,
                        sellAmount,
                        sellCurrency,
                        receiveAmount,
                        receiveCurrency,
                        commissionFeeText
                    )

            } else {
                val commissionFeeText =
                    if (_commissionFee.value > 0) stringResourceProvider.getString(
                        R.string.commission_fee_fail,
                        _commissionFee.value,
                        sellCurrency
                    ) else ""
                _conversionMessage.value =
                    "${
                        stringResourceProvider.getString(
                            R.string.conversion_failed,
                            sellCurrency
                        )
                    } $commissionFeeText"
            }
            loadAccountInfo()
        }
    }

    override fun onCleared() {
        WorkManager.getInstance(context).cancelAllWorkByTag("myWorkManager")
        super.onCleared()
    }
}