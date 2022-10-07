package com.androidrey.currencyexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Conversion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sellCurrency: String,
    val receiveCurrency: String,
    val sellAmount: Double,
    val receiveAmount: Double
)
