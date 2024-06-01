package com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model

data class VoiceSettingsActions(
    val setSyntheticVoice: (SyntheticVoiceOption) -> Unit = {},
    val setVoiceTrainingCompleted: (Boolean) -> Unit = {},
    val setUseTrainedVoice: (Boolean) -> Unit = {},
    val continueAction: () -> Unit = {}
)