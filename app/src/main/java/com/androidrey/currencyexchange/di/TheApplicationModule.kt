package com.androidrey.currencyexchange.di

import android.content.Context
import com.androidrey.currencyexchange.TheApplication
import com.androidrey.currencyexchange.datasource.RateResponseHelper
import com.androidrey.currencyexchange.datasource.RateResponseHelperImpl
import com.androidrey.currencyexchange.datasource.RateResponseInterface
import com.androidrey.currencyexchange.datasource.TheDatabase
import com.androidrey.currencyexchange.datasource.getDatabase
import com.androidrey.currencyexchange.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TheApplicationModule {

    @Provides
    @Singleton
    fun getContext() = TheApplication.instance

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL).build()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): TheDatabase =
        getDatabase(context)

    @Provides
    @Singleton
    fun provideRateResponseApi(retrofit: Retrofit): RateResponseInterface =
        retrofit.create(RateResponseInterface::class.java)

    @Provides
    @Singleton
    fun provideUserApiHelper(rateResponseHelperImpl: RateResponseHelperImpl): RateResponseHelper =
        rateResponseHelperImpl

    @Provides
    fun provideCurrencyDao(database: TheDatabase) = database.currencyDao
}