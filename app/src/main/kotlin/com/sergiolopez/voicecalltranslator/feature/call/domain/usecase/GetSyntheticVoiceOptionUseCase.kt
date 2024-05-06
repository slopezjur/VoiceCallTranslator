package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.datastore.VoiceSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.SyntheticVoiceOption
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