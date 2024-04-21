package com.sergiolopez.voicecalltranslator.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class VctModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context.applicationContext
}