package com.sergiolopez.voicecalltranslator

import android.app.Application
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.UserMapper
import com.sergiolopez.voicecalltranslator.feature.splash.Initializer
import com.sergiolopez.voicecalltranslator.feature.splash.InitializerManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class VoiceCallTranslatorApplication : Application() {

    @Inject
    lateinit var initializerManager: InitializerManager

    override fun onCreate() {
        super.onCreate()

        initializerManager.init(this)
    }
}