package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sergiolopez.voicecalltranslator.R

@Composable
internal fun LogoutView(
    modifier: Modifier = Modifier,
    logout: () -> Unit
) {
    Text(
        text = stringResource(id = R.string.voice_training),
        color = MaterialTheme.colorScheme.primary
    )
    Button(
        onClick = {
            logout.invoke()
        },
        modifier = modifier
    ) {
        Text(
            text =
            stringResource(R.string.logout),
            fontSize = 16.sp
        )
    }
}