package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.voice.data.datastore.VoiceSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.SyntheticVoiceOption
import javax.inject.Inject

class GetSyntheticVoiceOptionUseCase @Inject constructor(
    private val voiceSettingsDataStore: VoiceSettingsDataStore
) {
    suspend fun invoke(userId: String): SyntheticVoiceOption? {
        return voiceSettingsDataStore.getVoiceSettings(
            userId = userId
        )?.syntheticVoiceOption
    }
}