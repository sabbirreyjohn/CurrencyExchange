package com.androidrey.currencyexchange.model

import java.io.Serializable

data class RateResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
    val success: Boolean,
    val timestamp: Int
) : Serializable