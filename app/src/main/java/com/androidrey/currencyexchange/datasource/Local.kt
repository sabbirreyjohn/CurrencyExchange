package com.androidrey.currencyexchange.datasource

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.androidrey.currencyexchange.model.Account
import com.androidrey.currencyexchange.model.Conversion
import com.androidrey.currencyexchange.model.Rate
import java.util.concurrent.Executors

@Dao
interface CurrencyDao {
    @Query("select * from Rate")
    suspend fun getRates(): MutableList<Rate>

    @Query("select rate from Rate where currency = :currency")
    suspend fun getRate(currency: String): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: MutableList<Rate>)

    @Query("select * from Account order by balance desc")
    suspend fun getAccountInfo(): MutableList<Account>

    @Query("select balance from Account where currency = :currency")
    suspend fun getAccountBalance(currency: String): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountInfo(accountInfo: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInitialBalance(accountInfo: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(conversion: Conversion) : Long

    @Query("SELECT COUNT(id) FROM Conversion")
    suspend fun getNumberOfConversion(): Int
}

@Database(entities = [Account::class, Rate::class, Conversion::class], version = 1)

abstract class TheDatabase : RoomDatabase() {
    abstract val currencyDao: CurrencyDao
}

private lateinit var INSTANCE: TheDatabase

fun getDatabase(context: Context): TheDatabase {
    synchronized(TheDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TheDatabase::class.java,
                "currency.db"
            ).createFromAsset("currency.db").build()
        }
    }
    return INSTANCE
}