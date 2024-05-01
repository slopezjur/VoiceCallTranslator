package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergiolopez.voicecalltranslator.R

@Composable
internal fun VoiceTrainingView(
    modifier: Modifier = Modifier,
    setVoiceTrainingCompleted: (Boolean) -> Unit,
    voiceTrainingCompleted: Boolean,
    setUseTrainedVoice: (Boolean) -> Unit,
    useTrainedVoice: Boolean
) {
    Text(
        text = stringResource(id = R.string.voice_training),
        color = MaterialTheme.colorScheme.primary
    )
    Button(
        onClick = {
            // TODO : start voice training flow
            setVoiceTrainingCompleted.invoke(!voiceTrainingCompleted)
        },
        modifier = modifier
    ) {
        Text(
            text = if (voiceTrainingCompleted) {
                stringResource(R.string.restart)
            } else {
                stringResource(R.string.start)
            },
            fontSize = 16.sp
        )
    }
    if (voiceTrainingCompleted) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.use_trained_voice),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = modifier.size(16.dp))
            Switch(
                checked = useTrainedVoice,
                onCheckedChange = {
                    setUseTrainedVoice.invoke(it)
                }
            )
        }
    }
}