package com.androidrey.currencyexchange.ui.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidrey.currencyexchange.model.Account
import com.androidrey.currencyexchange.model.Rates
import com.androidrey.currencyexchange.repository.RatesRepository
import com.androidrey.currencyexchange.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: RatesRepository) : ViewModel() {

    private val _accountInfo= MutableStateFlow<MutableList<Account>>(mutableListOf())
    val accountInfo get() = _accountInfo

    init {
        loadRatesData()
    }

    private fun loadRatesData() {
        viewModelScope.launch {

            when (val status = repo.getRateResponseFromServer()) {
                is Status.Success -> {
                    //val accountAInfo: MutableList<Account> = mutableListOf()

                    //repo.insertAccountInfoToLocalDB(accountAInfo)
                    //repo.insertRatesToLocalDB(status.data?.rates!!)
                    loadRatesFromDB()
                }
                is Status.Error -> {
                    loadRatesFromDB()
                }
                else -> {

                }
            }
        }
    }

    private suspend fun loadRatesFromDB() {
        val dbStatus = repo.getAccountInfoFromLocalDB()
        when (dbStatus) {
            is Status.Success -> {
                _accountInfo.value = dbStatus.data!!
            }
            is Status.Error -> {
                _accountInfo.value = mutableListOf()
            }
        }
    }

}