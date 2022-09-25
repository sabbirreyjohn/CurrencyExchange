package com.androidrey.currencyexchange.datasource

import com.androidrey.currencyexchange.model.Account

class InitialBalanceGenerator {
    companion object {
        fun getInitialBalance(): Account {
            return Account(1, "EUR", 1.0, 1000.0)
        }
    }
}