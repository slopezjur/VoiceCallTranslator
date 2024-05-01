package com.sergiolopez.voicecalltranslator.di

import android.content.Context
import com.sergiolopez.voicecalltranslator.feature.call.domain.WebRtcManager
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.AnswerCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
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

    @Provides
    fun provideWebRtcManager(
        webRTCClient: WebRTCClient,
        firebaseAuthService: FirebaseAuthService,
        sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
        answerCallUseCase: AnswerCallUseCase
    ) = WebRtcManager(
        webRTCClient = webRTCClient,
        firebaseAuthService = firebaseAuthService,
        sendConnectionUpdateUseCase = sendConnectionUpdateUseCase,
        answerCallUseCase = answerCallUseCase
    )
}