package com.androidrey.currencyexchange.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes stringResId: Int): String {
        return context.getString(stringResId)
    }

    fun getString(@StringRes stringResId: Int, amount: Double, currency: String): String {
        return context.getString(stringResId, amount, currency)
    }

    fun getString(@StringRes stringResId: Int,currency: String): String {
        return context.getString(stringResId, currency)
    }

    fun getString(
        @StringRes stringResId: Int,
        amount1: Double,
        currency1: String,
        amount2: Double,
        currency2: String,
        commission: String
    ): String {
        return context.getString(stringResId, amount1, currency1, amount2, currency2, commission)
    }
}