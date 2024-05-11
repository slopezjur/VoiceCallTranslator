package com.sergiolopez.voicecalltranslator.feature.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun LoadingView(
    modifier: Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.1f),
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = modifier.size(16.dp))
        LinearProgressIndicator()
    }
}

@PreviewLightDark
@Composable
fun ContactCallDialogPreview() {
    VoiceCallTranslatorPreview {
        LoadingView(
            modifier = Modifier
        )
    }
}