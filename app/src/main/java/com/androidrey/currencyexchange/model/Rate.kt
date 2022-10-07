package com.androidrey.currencyexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rate(
    @PrimaryKey
    val currency: String,
    val rate: Double
)
