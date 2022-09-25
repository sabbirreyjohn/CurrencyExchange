package com.androidrey.currencyexchange.repository

import com.androidrey.currencyexchange.datasource.RateResponseHelper
import com.androidrey.currencyexchange.datasource.TheDatabase
import com.androidrey.currencyexchange.model.Account
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

    suspend fun getAccountInfoFromLocalDB(): Status<MutableList<Account>> {
        val response = try {
            database.accountInfoDao.getAccountInfo()
        } catch (e: Exception) {
            return Status.Error("Failed to load from database")
        }
        return Status.Success(response)
    }

    suspend fun insertAccountInfoToLocalDB(accountInfo: MutableList<Account>) =
        database.accountInfoDao.insertAccountInfo(accountInfo)

}
