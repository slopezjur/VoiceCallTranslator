package com.sergiolopez.voicecalltranslator.feature.settings.voice

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.SyntheticVoiceOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VoiceSettingsViewModel @Inject constructor(
) : VoiceCallTranslatorViewModel() {

    private val _voiceSettingsDataStateState = MutableStateFlow(VoiceSettingsData())
    val voiceSettingsDataState: StateFlow<VoiceSettingsData>
        get() = _voiceSettingsDataStateState.asStateFlow()

    fun setSyntheticVoice(syntheticVoiceOption: SyntheticVoiceOption) {
        _voiceSettingsDataStateState.value = _voiceSettingsDataStateState.value.copy(
            syntheticVoiceOption = syntheticVoiceOption
        )
    }

    fun setVoiceTrainingCompleted(voiceTrainingCompleted: Boolean) {
        _voiceSettingsDataStateState.value = _voiceSettingsDataStateState.value.copy(
            voiceTrainingCompleted = voiceTrainingCompleted
        )
    }

    fun setUseTrainedVoice(useTrainedVoice: Boolean) {
        _voiceSettingsDataStateState.value = _voiceSettingsDataStateState.value.copy(
            useTrainedVoice = useTrainedVoice
        )
    }
}
