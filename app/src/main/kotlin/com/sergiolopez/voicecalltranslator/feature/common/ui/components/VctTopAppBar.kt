package com.sergiolopez.voicecalltranslator.feature.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun VctTopAppBar(
    modifier: Modifier,
    titleName: Int,
    hasNavigation: Boolean,
    hasAction: Boolean,
    navigatePopBackStack: () -> Unit,
    content: @Composable () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(titleName),
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            if (hasNavigation) {
                IconButton(
                    onClick = {
                        navigatePopBackStack.invoke()
                    },
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Action")
                }
            } else {
                SetEmptyIconButton()
            }
        },
        actions = {
            if (hasAction) {
                content()
            } else {
                SetEmptyIconButton()
            }
        }
    )
}

@Composable
private fun SetEmptyIconButton() {
    // Note : Invisible button to generate the exact space to center the title depending on the screen
    IconButton(
        onClick = {},
        enabled = false
    ) {}
}

@PreviewLightDark
@Composable
private fun VctTopAppBarPreview() {
    VoiceCallTranslatorPreview {
        VctTopAppBar(
            modifier = Modifier,
            titleName = R.string.app_name,
            hasNavigation = false,
            hasAction = false,
            navigatePopBackStack = {},
            content = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun VctTopAppBarHasNavigationPreview() {
    VoiceCallTranslatorPreview {
        VctTopAppBar(
            modifier = Modifier,
            titleName = R.string.app_name,
            hasNavigation = true,
            hasAction = false,
            navigatePopBackStack = {},
            content = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun VctTopAppBarHasActionPreview() {
    VoiceCallTranslatorPreview {
        VctTopAppBar(
            modifier = Modifier,
            titleName = R.string.app_name,
            hasNavigation = false,
            hasAction = true,
            navigatePopBackStack = {},
            content = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(Icons.Filled.MoreVert, "Back Action")
                }
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun VctTopAppBarHasNavigationAndActionPreview() {
    VoiceCallTranslatorPreview {
        VctTopAppBar(
            modifier = Modifier,
            titleName = R.string.app_name,
            hasNavigation = true,
            hasAction = true,
            navigatePopBackStack = {},
            content = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(Icons.Filled.MoreVert, "Back Action")
                }
            }
        )
    }
}