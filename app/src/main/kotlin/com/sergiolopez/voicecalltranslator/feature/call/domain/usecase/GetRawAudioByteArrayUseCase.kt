package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.voice.data.datastore.VoiceSettingsDataStore
import javax.inject.Inject

class GetRawAudioByteArrayUseCase @Inject constructor(
    private val voiceSettingsDataStore: VoiceSettingsDataStore
) {
    suspend fun invoke(): ByteArray? {
        return voiceSettingsDataStore.getByteArray()
    }
}