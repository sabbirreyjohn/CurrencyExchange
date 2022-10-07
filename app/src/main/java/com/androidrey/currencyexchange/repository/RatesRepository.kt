package com.androidrey.currencyexchange.repository

import com.androidrey.currencyexchange.datasource.InitialBalanceGenerator
import com.androidrey.currencyexchange.datasource.RateResponseHelper
import com.androidrey.currencyexchange.datasource.TheDatabase
import com.androidrey.currencyexchange.model.Account
import com.androidrey.currencyexchange.model.Conversion
import com.androidrey.currencyexchange.model.Rate
import com.androidrey.currencyexchange.model.RateResponse
import com.androidrey.currencyexchange.util.Status
import javax.inject.Inject

class RatesRepository @Inject internal constructor(
    private val database: TheDatabase,
    private val rateResponseHelper: RateResponseHelper
) {
    suspend fun getRateResponseFromServer(): Status<RateResponse> {
        val response = try {
            rateResponseHelper.getRateResponse()
        } catch (e: Exception) {
            return Status.Error("An unknown error occured.")
        }
        return Status.Success(response)
    }

    suspend fun insertAccountInfoToLocalDB(accountInfo: Account) =
        database.currencyDao.insertAccountInfo(accountInfo)

    suspend fun insertInitialBalance() =
        database.currencyDao.insertInitialBalance(InitialBalanceGenerator.getInitialBalance())

    suspend fun getAccountInfo(): Status<MutableList<Account>> {
        val response = try {
            database.currencyDao.getAccountInfo()
        } catch (e: Exception) {
            return Status.Error("Failed to load from database")
        }
        return Status.Success(response)
    }

    suspend fun insertRatesToLocalDB(rates: MutableList<Rate>) =
        database.currencyDao.insertRates(rates)

    suspend fun insertConversionToLocalDB(conversion: Conversion) =
        database.currencyDao.insertConversion(conversion)

    suspend fun getRates(): Status<MutableList<Rate>> {
        val response = try {
            database.currencyDao.getRates()
        } catch (e: Exception) {
            return Status.Error("Failed to load from database")
        }
        return Status.Success(response)
    }

    suspend fun getAccountBalance(currency: String) =
        database.currencyDao.getAccountBalance(currency)

    suspend fun getRate(currency: String) = database.currencyDao.getRate(currency)

    suspend fun getNumberOfConversion() =
        database.currencyDao.getNumberOfConversion()
}
