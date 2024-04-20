package com.sergiolopez.voicecalltranslator.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun VoiceCallTranslatorPreview(content: @Composable () -> Unit) {
    VoiceCallTranslatorTheme {
        Surface {
            content()
        }
    }
}