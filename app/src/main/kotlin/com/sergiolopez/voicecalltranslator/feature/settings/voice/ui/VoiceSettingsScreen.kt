package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.common.ui.components.VctTopAppBar
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.SyntheticVoiceOption
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsActions
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsData
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun VoiceSettingsScreen(
    openAndPopUp: () -> Unit,
    voiceSettingsViewModel: VoiceSettingsViewModel = hiltViewModel()
) {
    VoiceSettingsScreenContent(
        openAndPopUp = openAndPopUp,
        dropDownExpanded = false,
        voiceSettingsData = voiceSettingsViewModel.voiceSettingsDataState.collectAsStateWithLifecycle().value,
        voiceSettingsActions = VoiceSettingsActions(
            setSyntheticVoice = { voiceSettingsViewModel.setSyntheticVoice(it) },
            setVoiceTrainingCompleted = { voiceSettingsViewModel.setVoiceTrainingCompleted(it) },
            setUseTrainedVoice = { voiceSettingsViewModel.setUseTrainedVoice(it) }
        )
    )
}

@Composable
private fun VoiceSettingsScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: () -> Unit,
    dropDownExpanded: Boolean,
    voiceSettingsData: VoiceSettingsData,
    voiceSettingsActions: VoiceSettingsActions
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        VctTopAppBar(
            modifier = modifier,
            titleName = R.string.voice_settings,
            hasNavigation = true,
            hasAction = false,
            openAndPopUp = openAndPopUp,
            content = {}
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SyntheticVoiceView(
                dropDownExpanded = dropDownExpanded,
                syntheticVoiceOption = voiceSettingsData.syntheticVoiceOption,
                setSyntheticVoice = voiceSettingsActions.setSyntheticVoice,
                useTrainedVoice = voiceSettingsData.useTrainedVoice
            )
            Spacer(modifier = modifier.size(16.dp))
            VoiceTrainingView(
                setVoiceTrainingCompleted = voiceSettingsActions.setVoiceTrainingCompleted,
                voiceTrainingCompleted = voiceSettingsData.voiceTrainingCompleted,
                setUseTrainedVoice = voiceSettingsActions.setUseTrainedVoice,
                useTrainedVoice = voiceSettingsData.useTrainedVoice
            )
        }
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentPreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = false,
            voiceSettingsData = VoiceSettingsData(),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            )
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentDropDownExpandedPreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            )
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentSyntheticVoiceMalePreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(
                syntheticVoiceOption = SyntheticVoiceOption.MALE
            ),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            )
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentVoiceTrainingCompletedMalePreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(
                voiceTrainingCompleted = true
            ),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            )
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentUseTrainedVoicePreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(
                voiceTrainingCompleted = true,
                useTrainedVoice = true
            ),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            )
        )
    }
}
