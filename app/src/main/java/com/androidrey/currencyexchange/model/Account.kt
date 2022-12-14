package com.androidrey.currencyexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Account(
    @PrimaryKey
    val currency: String,
    val balance: Double
) : Serializable
