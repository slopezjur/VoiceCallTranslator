package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

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
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsActions
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun AccountSettingsScreen(
    openAndPopUp: () -> Unit,
    accountSettingsViewModel: AccountSettingsViewModel = hiltViewModel()
) {
    AccountSettingsScreenContent(
        openAndPopUp = openAndPopUp,
        dropDownExpanded = false,
        accountSettingsData = accountSettingsViewModel.accountSettingsDataState.collectAsState().value,
        accountSettingsAction = AccountSettingsActions(
            setLanguage = { accountSettingsViewModel.setLanguage(it) },
            setTheme = { accountSettingsViewModel.setTheme(it) },
            logout = { accountSettingsViewModel.logout() },
            deleteAccount = { accountSettingsViewModel.deleteAccount() }
        )
    )
}

@Composable
private fun AccountSettingsScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: () -> Unit,
    dropDownExpanded: Boolean,
    accountSettingsData: AccountSettingsData,
    accountSettingsAction: AccountSettingsActions
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        VtcTopAppBar(
            modifier = modifier,
            titleName = R.string.account_settings,
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
            Spacer(modifier = modifier.size(16.dp))
            LogoutView(
                modifier = modifier,
                logout = accountSettingsAction.logout
            )
        }
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentPreview() {
    VoiceCallTranslatorPreview {
        AccountSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = false,
            accountSettingsData = AccountSettingsData(),
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            )
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentDropDownExpandedPreview() {
    VoiceCallTranslatorPreview {
        AccountSettingsScreenContent(
            modifier = Modifier,
            openAndPopUp = {},
            dropDownExpanded = true,
            accountSettingsData = AccountSettingsData(),
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            )
        )
    }
}
