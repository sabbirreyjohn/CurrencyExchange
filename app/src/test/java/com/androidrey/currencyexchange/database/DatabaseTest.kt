package com.androidrey.currencyexchange.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidrey.currencyexchange.datasource.TheDatabase
import com.androidrey.currencyexchange.model.Account
import com.androidrey.currencyexchange.model.Rate
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class DatabaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TheDatabase
    private lateinit var rates: MutableList<Rate>
    private lateinit var accountInfo: MutableList<Account>

    @Before
    fun initDb() {

        accountInfo = mutableListOf(
            Account("EUR", 1000.00)
        )

        rates = mutableListOf(
            Rate("EUR", 1.0),
            Rate("JPY", 118.52),
            Rate("USD", 1.103),
            Rate("GBP", 0.90155)
        )

        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TheDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun `inserts rates successfully`() {
        runBlockingTest {
            database.currencyDao.insertAccountInfo(accountInfo[0])
            database.currencyDao.insertRates(rates)
            val rates = database.currencyDao.getRates()
            assertThat(rates.size).isEqualTo(4)
        }
    }

    @Test
    fun `updates balance if a currency is already exist`() {
        runBlockingTest {
            database.currencyDao.insertAccountInfo(accountInfo[0])
            assertThat(database.currencyDao.getAccountBalance("EUR")).isEqualTo(1000.00)
            database.currencyDao.insertAccountInfo(Account("EUR", 2000.00))
            assertThat(database.currencyDao.getAccountBalance("EUR")).isEqualTo(2000.00)
        }
    }
}