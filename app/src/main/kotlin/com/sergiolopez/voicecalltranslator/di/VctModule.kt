package com.sergiolopez.voicecalltranslator.di

import android.content.Context
import android.media.AudioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class VctModule {

    @Provides
    fun providesContext(@ApplicationContext context: Context): Context = context.applicationContext

    @Provides
    fun providesAudioManager(context: Context): AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
}