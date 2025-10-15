package com.example.binancews.di

import com.example.binancews.data.BinanceRepositoryImpl
import com.example.binancews.domain.BinanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBinanceRepository(
        impl: BinanceRepositoryImpl
    ): BinanceRepository
}