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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.common.ui.components.VtcTopAppBar
import com.sergiolopez.voicecalltranslator.feature.settings.voice.VoiceSettingsViewModel
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsData
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun VoiceSettingsScreen(
    openAndPopUp: () -> Unit,
    voiceSettingsViewModel: VoiceSettingsViewModel = hiltViewModel()
) {
    val voiceSettingsData = voiceSettingsViewModel.voiceSettingsDataState.collectAsState().value

    // TODO : Use actions
    val setSyntheticVoice: (SyntheticVoiceOption) -> Unit = {
        voiceSettingsViewModel.setSyntheticVoice(it)
    }
    val setVoiceTrainingCompleted: (Boolean) -> Unit = {
        voiceSettingsViewModel.setVoiceTrainingCompleted(it)
    }
    val setUseTrainedVoice: (Boolean) -> Unit = {
        voiceSettingsViewModel.setUseTrainedVoice(it)
    }

    VoiceSettingsScreenContent(
        openAndPopUp = openAndPopUp,
        dropDownExpanded = false,
        voiceSettingsData = voiceSettingsData,
        setSyntheticVoice = setSyntheticVoice,
        setVoiceTrainingCompleted = setVoiceTrainingCompleted,
        setUseTrainedVoice = setUseTrainedVoice
    )
}

@Composable
private fun VoiceSettingsScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: () -> Unit,
    dropDownExpanded: Boolean,
    voiceSettingsData: VoiceSettingsData,
    setSyntheticVoice: (SyntheticVoiceOption) -> Unit,
    setVoiceTrainingCompleted: (Boolean) -> Unit,
    setUseTrainedVoice: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        VtcTopAppBar(
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
                setSyntheticVoice = setSyntheticVoice,
                useTrainedVoice = voiceSettingsData.useTrainedVoice
            )
            Spacer(modifier = modifier.size(16.dp))
            VoiceTrainingView(
                setVoiceTrainingCompleted = setVoiceTrainingCompleted,
                voiceTrainingCompleted = voiceSettingsData.voiceTrainingCompleted,
                setUseTrainedVoice = setUseTrainedVoice,
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
            setSyntheticVoice = {},
            setVoiceTrainingCompleted = {},
            setUseTrainedVoice = {}
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
            setSyntheticVoice = {},
            setVoiceTrainingCompleted = {},
            setUseTrainedVoice = {}
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
            setSyntheticVoice = {},
            setVoiceTrainingCompleted = {},
            setUseTrainedVoice = {}
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
            setSyntheticVoice = {},
            setVoiceTrainingCompleted = {},
            setUseTrainedVoice = {}
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
            setSyntheticVoice = {},
            setVoiceTrainingCompleted = {},
            setUseTrainedVoice = {}
        )
    }
}
