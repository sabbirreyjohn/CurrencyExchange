package com.androidrey.currencyexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val currency: String,
    val rate: Double,
    val balance: Double
) : Serializable
