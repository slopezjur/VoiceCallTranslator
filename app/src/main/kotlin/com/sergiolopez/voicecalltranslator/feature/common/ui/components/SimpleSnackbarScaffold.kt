package com.sergiolopez.voicecalltranslator.feature.common.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SimpleSnackbarScaffold(
    content: @Composable (PaddingValues, (String) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarLauncher: (String) -> Unit = {
        showSnackbar(
            scope = scope,
            snackbarHostState = snackbarHostState,
            message = it
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        content(innerPadding, snackbarLauncher)
    }
}

fun showSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String
) {
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}

@PreviewLightDark
@Composable
private fun SnackbarWithScaffoldPreview() {
    VoiceCallTranslatorTheme {
        SimpleSnackbarScaffold(
            content = { _, _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun SnackbarWithScaffoldShowPreview() {
    VoiceCallTranslatorTheme {
        SimpleSnackbarScaffold(
            content = { _, showSnakbar ->
                showSnakbar.invoke("Text")
            }
        )
    }
}