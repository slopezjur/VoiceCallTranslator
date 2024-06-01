package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.common.ui.components.VctTopAppBar
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.SyntheticVoiceOption
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsActions
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.navigation.NavigationVoiceSettings
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun VoiceSettingsScreen(
    navigationVoiceSettings: NavigationVoiceSettings,
    firstStartUp: Boolean,
    voiceSettingsViewModel: VoiceSettingsViewModel = hiltViewModel()
) {
    VoiceSettingsScreenContent(
        navigatePopBackStack = navigationVoiceSettings.navigatePopBackStack,
        dropDownExpanded = false,
        voiceSettingsData = voiceSettingsViewModel.voiceSettingsDataState.collectAsStateWithLifecycle().value,
        voiceSettingsActions = VoiceSettingsActions(
            setSyntheticVoice = { voiceSettingsViewModel.setSyntheticVoice(it) },
            setVoiceTrainingCompleted = { voiceSettingsViewModel.setVoiceTrainingCompleted(it) },
            setUseTrainedVoice = { voiceSettingsViewModel.setUseTrainedVoice(it) },
            continueAction = {
                voiceSettingsViewModel.continueAction(
                    navigationVoiceSettings.clearAndNavigate
                )
            },
        ),
        firstStartUp = firstStartUp
    )
}

@Composable
private fun VoiceSettingsScreenContent(
    modifier: Modifier = Modifier,
    navigatePopBackStack: () -> Unit,
    dropDownExpanded: Boolean,
    voiceSettingsData: VoiceSettingsData,
    voiceSettingsActions: VoiceSettingsActions,
    firstStartUp: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        VctTopAppBar(
            modifier = modifier,
            titleName = R.string.voice_settings,
            hasNavigation = !firstStartUp,
            hasAction = false,
            navigatePopBackStack = setUpNavigatePopBackStack(
                firstStartUp = firstStartUp,
                navigatePopBackStack = navigatePopBackStack
            ),
            content = {}
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                SyntheticVoiceView(
                    dropDownExpanded = dropDownExpanded,
                    syntheticVoiceOption = voiceSettingsData.syntheticVoiceOption,
                    setSyntheticVoice = voiceSettingsActions.setSyntheticVoice,
                    useTrainedVoice = voiceSettingsData.useTrainedVoice
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                VoiceTrainingView(
                    setVoiceTrainingCompleted = voiceSettingsActions.setVoiceTrainingCompleted,
                    voiceTrainingCompleted = voiceSettingsData.voiceTrainingCompleted,
                    setUseTrainedVoice = voiceSettingsActions.setUseTrainedVoice,
                    useTrainedVoice = voiceSettingsData.useTrainedVoice
                )
            }
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (firstStartUp) {
                Button(
                    onClick = {
                        voiceSettingsActions.continueAction.invoke()
                    },
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.continue_action),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun setUpNavigatePopBackStack(
    firstStartUp: Boolean,
    navigatePopBackStack: () -> Unit
) = if (!firstStartUp) {
    navigatePopBackStack
} else {
    {}
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentPreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            dropDownExpanded = false,
            voiceSettingsData = VoiceSettingsData(),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            ),
            firstStartUp = true
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentDropDownExpandedPreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            ),
            firstStartUp = false
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentSyntheticVoiceMalePreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(
                syntheticVoiceOption = SyntheticVoiceOption.MALE
            ),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            ),
            firstStartUp = false
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentVoiceTrainingCompletedMalePreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(
                voiceTrainingCompleted = true
            ),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            ),
            firstStartUp = false
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentUseTrainedVoicePreview() {
    VoiceCallTranslatorPreview {
        VoiceSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            dropDownExpanded = true,
            voiceSettingsData = VoiceSettingsData(
                voiceTrainingCompleted = true,
                useTrainedVoice = true
            ),
            voiceSettingsActions = VoiceSettingsActions(
                setSyntheticVoice = {},
                setVoiceTrainingCompleted = {},
                setUseTrainedVoice = {}
            ),
            firstStartUp = false
        )
    }
}
