package com.androidrey.currencyexchange.datasource

import com.androidrey.currencyexchange.model.RateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import javax.inject.Inject

interface RateResponseInterface {
    @Headers("apikey: HBhvWzrB3TBTh6k4C3nK6ybfggmoRrn4")
    @GET("exchangerates_data/latest")
    suspend fun getRateResponse(): RateResponse
}

interface RateResponseHelper {
    suspend fun getRateResponse(): RateResponse
}

class RateResponseHelperImpl @Inject internal constructor(private val rateResponseInterface: RateResponseInterface) :
    RateResponseHelper {
    override suspend fun getRateResponse(): RateResponse {
        return rateResponseInterface.getRateResponse()
    }
}
