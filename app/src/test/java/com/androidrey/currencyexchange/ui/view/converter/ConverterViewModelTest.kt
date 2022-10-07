package com.androidrey.currencyexchange.ui.view.converter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidrey.currencyexchange.MainCoroutineRule
import com.androidrey.currencyexchange.di.StringResourceProvider
import com.androidrey.currencyexchange.model.Rate
import com.androidrey.currencyexchange.repository.RatesRepository
import com.androidrey.currencyexchange.util.Status
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class ConverterViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var converterViewModel: ConverterViewModel
    private lateinit var mRepository: RatesRepository
    private lateinit var mStringProvider: StringResourceProvider

    @Before
    fun setUp() {
        mRepository = mockk()
        mStringProvider = mockk()
        converterViewModel = ConverterViewModel(mRepository, mStringProvider)

        coEvery { mRepository.getRate("EUR") } returns 1.0
        coEvery { mRepository.getRate("JPY") } returns 118.52
        coEvery { mRepository.getRate("USD") } returns 1.103
        coEvery { mRepository.getRate("GBP") } returns 0.90155
        coEvery { mRepository.getAccountBalance("EUR") } returns 1000.0
    }

    private val testDispatcher = TestCoroutineDispatcher()

    @Test
    fun `loads rates`() = testDispatcher.runBlockingTest {
        var rate = Rate("EUR", 1.0)
        var list = mutableListOf<Rate>()
        list.add(rate)
        coEvery { mRepository.getRates() } returns Status.Success(list)
        converterViewModel.loadRatesFromDb()
        assertThat(converterViewModel.rates.value.size).isEqualTo(1)
    }

    @Test
    fun `converts currency without commission fee`() = testDispatcher.runBlockingTest {
        coEvery { mRepository.getNumberOfConversion() } returns 4
        converterViewModel.convertCurrency("EUR", "USD", 100.0)
        converterViewModel.submitConvertedCurrency(
            "EUR",
            100.0,
            "USD",
            converterViewModel.receiveAmount.value
        )
        assertThat(converterViewModel.commissionFee.value).isEqualTo(0.0)
        assertThat(converterViewModel.receiveAmount.value).isEqualTo(110.3)
    }

    @Test
    fun `converts currency with commission fee after 5 conversion`() =
        testDispatcher.runBlockingTest {

            coEvery { mRepository.getNumberOfConversion() } returns 6
            converterViewModel.convertCurrency("EUR", "USD", 100.0)
            converterViewModel.submitConvertedCurrency(
                "EUR",
                100.0,
                "USD",
                converterViewModel.receiveAmount.value
            )
            assertThat(converterViewModel.commissionFee.value).isEqualTo(0.70)
            assertThat(converterViewModel.receiveAmount.value).isEqualTo(110.3)

        }

    @Test
    fun `converts currency with commission fee when sell amount is more than 200`() =
        testDispatcher.runBlockingTest {

            coEvery { mRepository.getNumberOfConversion() } returns 4
            converterViewModel.convertCurrency("EUR", "USD", 100.0)
            converterViewModel.submitConvertedCurrency(
                "EUR",
                300.0,
                "USD",
                converterViewModel.receiveAmount.value
            )
            assertThat(converterViewModel.commissionFee.value).isEqualTo(2.10)
            assertThat(converterViewModel.receiveAmount.value).isEqualTo(110.3)

        }
}