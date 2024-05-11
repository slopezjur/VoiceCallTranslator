package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.settings.voice.data.datastore.VoiceSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.SyntheticVoiceOption
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VoiceSettingsViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val voiceSettingsDataStore: VoiceSettingsDataStore
) : VoiceCallTranslatorViewModel() {

    private val _voiceSettingsDataStateState = MutableStateFlow(VoiceSettingsData())
    val voiceSettingsDataState: StateFlow<VoiceSettingsData>
        get() = _voiceSettingsDataStateState.asStateFlow()

    private val user = firebaseAuthRepository.currentUser.value

    init {
        loadVoiceSettings()
    }

    private fun loadVoiceSettings() {
        launchCatching {
            user?.let { user ->
                voiceSettingsDataStore.getVoiceSettings(
                    userId = user.id
                )?.let {
                    _voiceSettingsDataStateState.value = it
                }
            }
        }
    }

    fun setSyntheticVoice(syntheticVoiceOption: SyntheticVoiceOption) {
        _voiceSettingsDataStateState.value = _voiceSettingsDataStateState.value.copy(
            syntheticVoiceOption = syntheticVoiceOption
        )
        setVoiceSettings()
    }

    fun setVoiceTrainingCompleted(voiceTrainingCompleted: Boolean) {
        _voiceSettingsDataStateState.value = _voiceSettingsDataStateState.value.copy(
            voiceTrainingCompleted = voiceTrainingCompleted
        )
        setVoiceSettings()
    }

    fun setUseTrainedVoice(useTrainedVoice: Boolean) {
        _voiceSettingsDataStateState.value = _voiceSettingsDataStateState.value.copy(
            useTrainedVoice = useTrainedVoice
        )
        setVoiceSettings()
    }

    private fun setVoiceSettings() {
        launchCatching {
            user?.id?.let { userId ->
                voiceSettingsDataStore.setVoiceSettings(
                    userId = userId,
                    voiceSettingsData = voiceSettingsDataState.value
                )
            }
        }
    }
}
