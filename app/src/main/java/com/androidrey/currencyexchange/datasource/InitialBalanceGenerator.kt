package com.androidrey.currencyexchange.datasource

import com.androidrey.currencyexchange.model.Account

class InitialBalanceGenerator {
    companion object {
        fun getInitialBalance(): Account {
            return Account("EUR", 1000.00)
        }
    }
}