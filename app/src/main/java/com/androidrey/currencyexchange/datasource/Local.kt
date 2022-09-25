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
import com.androidrey.currencyexchange.model.Rates
import java.util.concurrent.Executors

@Dao
interface AccountInfoDao {
    @Query("select * from Account")
    suspend fun getAccountInfo(): MutableList<Account>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountInfo(accountInfo: MutableList<Account>)

    @Insert
    fun insertInitialBalance(accountInfo: Account)
}

@Database(entities = [Account::class], version = 1)

abstract class TheDatabase : RoomDatabase() {
    abstract val accountInfoDao: AccountInfoDao
}

private lateinit var INSTANCE: TheDatabase

fun getDatabase(context: Context): TheDatabase {
    synchronized(TheDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TheDatabase::class.java,
                "accountInfo.db"
            ).fallbackToDestructiveMigration().addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Executors.newSingleThreadExecutor().execute {
                        INSTANCE.accountInfoDao.insertInitialBalance(InitialBalanceGenerator.getInitialBalance())
                    }
                }
            }).build()
        }
    }
    return INSTANCE
}