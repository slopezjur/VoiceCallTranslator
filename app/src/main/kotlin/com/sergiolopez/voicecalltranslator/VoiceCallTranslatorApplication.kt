package com.sergiolopez.voicecalltranslator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class VoiceCallTranslatorApplication : Application() {

    /*@Inject
    lateinit var initializerManager: InitializerManager*/

    override fun onCreate() {
        super.onCreate()

        //initializerManager.init(this)
    }
}