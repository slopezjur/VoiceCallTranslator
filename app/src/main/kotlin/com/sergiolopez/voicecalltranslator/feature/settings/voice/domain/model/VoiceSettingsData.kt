package com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model

import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.SyntheticVoiceOption
import kotlinx.serialization.Serializable

@Serializable
data class VoiceSettingsData(
    val syntheticVoiceOption: SyntheticVoiceOption = SyntheticVoiceOption.NONE,
    val voiceTrainingCompleted: Boolean = false,
    val useTrainedVoice: Boolean = false
)