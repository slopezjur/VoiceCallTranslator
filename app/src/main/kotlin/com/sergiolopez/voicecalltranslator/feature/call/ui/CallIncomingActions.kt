package com.sergiolopez.voicecalltranslator.feature.call.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallAction
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
internal fun CallIncomingActions(
    modifier: Modifier = Modifier,
    onCallAction: (CallAction) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .shadow(1.dp)
            .padding(26.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FloatingActionButton(
            onClick = {
                onCallAction(
                    CallAction.Disconnect
                )
            },
            containerColor = MaterialTheme.colorScheme.error,
        ) {
            Icon(
                imageVector = Icons.Rounded.Call,
                contentDescription = null,
                modifier = modifier.rotate(90f),
            )
        }
        FloatingActionButton(
            onClick = {
                onCallAction(
                    CallAction.Answer
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(imageVector = Icons.Rounded.Call, contentDescription = null)
        }
    }
}

@PreviewLightDark
@Composable
fun CallIncomingActionsPreview() {
    VoiceCallTranslatorPreview {
        CallIncomingActions(
            onCallAction = {}
        )
    }
}