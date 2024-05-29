package com.sergiolopez.voicecalltranslator.feature.call.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.SpatialAudioOff
import androidx.compose.material.icons.rounded.SpeakerPhone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallAction
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
internal fun CallControls(
    modifier: Modifier = Modifier,
    isMuted: Boolean,
    isSpeaker: Boolean,
    onCallAction: (CallAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconToggleButton(
            checked = isMuted,
            onCheckedChange = {
                onCallAction(
                    CallAction.ToggleMute(
                        isMuted = it
                    )
                )
            },
        ) {
            if (isMuted) {
                Icon(imageVector = Icons.Rounded.MicOff, contentDescription = "Mic on")
            } else {
                Icon(imageVector = Icons.Rounded.Mic, contentDescription = "Mic off")
            }
        }
        IconToggleButton(
            checked = isSpeaker,
            onCheckedChange = {
                onCallAction(
                    CallAction.ToggleSpeaker(
                        isSpeaker = it
                    )
                )
            },
        ) {
            if (isSpeaker) {
                Icon(imageVector = Icons.Rounded.SpeakerPhone, contentDescription = "Speaker on")
            } else {
                Icon(
                    imageVector = Icons.Rounded.SpatialAudioOff,
                    contentDescription = "Speaker off"
                )
            }
        }

        FloatingActionButton(
            onClick = {
                onCallAction(
                    CallAction.Disconnect,
                )
            },
            containerColor = MaterialTheme.colorScheme.error,
        ) {
            Icon(
                imageVector = Icons.Rounded.Call,
                contentDescription = "Disconnect call",
                modifier = modifier.rotate(90f),
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CallControlsPreview() {
    VoiceCallTranslatorPreview {
        CallControls(
            isMuted = false,
            isSpeaker = false,
            onCallAction = {}
        )
    }
}

@PreviewLightDark
@Composable
fun CallControlsIsMutedPreview() {
    VoiceCallTranslatorPreview {
        CallControls(
            isMuted = true,
            isSpeaker = false,
            onCallAction = {}
        )
    }
}

@PreviewLightDark
@Composable
fun CallControlsIsSpeakerPreview() {
    VoiceCallTranslatorPreview {
        CallControls(
            isMuted = false,
            isSpeaker = true,
            onCallAction = {}
        )
    }
}