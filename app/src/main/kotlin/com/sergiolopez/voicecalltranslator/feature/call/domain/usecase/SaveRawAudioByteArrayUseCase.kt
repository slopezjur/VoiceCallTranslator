package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.voice.data.datastore.VoiceSettingsDataStore
import javax.inject.Inject

class SaveRawAudioByteArrayUseCase @Inject constructor(
    private val voiceSettingsDataStore: VoiceSettingsDataStore
) {
    suspend fun invoke(byteArray: ByteArray) {
        return voiceSettingsDataStore.saveByteArray(
            byteArray = byteArray
        )
    }
}