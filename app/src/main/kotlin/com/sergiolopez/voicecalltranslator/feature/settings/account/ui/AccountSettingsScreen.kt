package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsActions
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.account.ui.navigation.NavigationAccountSettings
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun AccountSettingsScreen(
    navigationAccountSettings: NavigationAccountSettings,
    firstStartUp: Boolean,
    accountSettingsViewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val accountSettingsDataState by accountSettingsViewModel.accountSettingsDataState.collectAsStateWithLifecycle()

    LaunchedEffect(accountSettingsDataState) {
        // NOTE : After updating the Locale, we have to reload the them with the User option
        navigationAccountSettings.setThemeConfiguration.invoke(accountSettingsDataState.themeOption)
    }

    AccountSettingsScreenContent(
        navigatePopBackStack = navigationAccountSettings.navigatePopBackStack,
        languageDropDownExpanded = false,
        themeDropDownExpanded = false,
        accountSettingsData = accountSettingsDataState,
        accountSettingsAction = AccountSettingsActions(
            setLanguage = {
                accountSettingsViewModel.setLanguage(it)
            },
            setTheme = {
                accountSettingsViewModel.setTheme(it)
            },
            continueAction = {
                accountSettingsViewModel.continueAction(
                    navigationAccountSettings.clearAndNavigate
                )
            },
            logout = {
                accountSettingsViewModel.logout(
                    navigationAccountSettings.clearAndNavigate
                )
            },
            deleteAccount = {
                accountSettingsViewModel.deleteAccount(
                    navigationAccountSettings.clearAndNavigate
                )
            }
        ),
        showDeleteAccountDialog = false,
        firstStartUp = firstStartUp
    )
}

@Composable
private fun AccountSettingsScreenContent(
    modifier: Modifier = Modifier,
    navigatePopBackStack: () -> Unit,
    languageDropDownExpanded: Boolean,
    themeDropDownExpanded: Boolean,
    accountSettingsData: AccountSettingsData,
    accountSettingsAction: AccountSettingsActions,
    showDeleteAccountDialog: Boolean,
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
            titleName = R.string.account_settings,
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
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = modifier.padding(16.dp)
                ) {
                    LanguageView(
                        modifier = modifier,
                        dropDownExpanded = languageDropDownExpanded,
                        languageOption = accountSettingsData.languageOption,
                        setLanguage = accountSettingsAction.setLanguage
                    )
                }
            }

            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = modifier.padding(16.dp)
                ) {
                    ThemeView(
                        modifier = modifier,
                        dropDownExpanded = themeDropDownExpanded,
                        themeOption = accountSettingsData.themeOption,
                        setTheme = accountSettingsAction.setTheme
                    )
                }
            }
        }

        SetBottomActions(
            modifier = modifier,
            firstStartUp = firstStartUp,
            accountSettingsAction = accountSettingsAction,
            showDeleteAccountDialog = showDeleteAccountDialog
        )
    }
}

@Composable
private fun SetBottomActions(
    modifier: Modifier,
    firstStartUp: Boolean,
    accountSettingsAction: AccountSettingsActions,
    showDeleteAccountDialog: Boolean
) {
    var showDeleteAccountDialogRemember by remember { mutableStateOf(showDeleteAccountDialog) }

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
                    accountSettingsAction.continueAction.invoke()
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.continue_action),
                    fontSize = 16.sp
                )
            }
        } else {
            Button(
                onClick = {
                    accountSettingsAction.logout.invoke()
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.logout),
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = {
                    showDeleteAccountDialogRemember = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.delete_account),
                    fontSize = 16.sp
                )
            }

            if (showDeleteAccountDialogRemember) {
                DeleteAccountDialog(
                    modifier = modifier,
                    onDeleteAccount = accountSettingsAction.deleteAccount,
                    onDismissDialog = {
                        showDeleteAccountDialogRemember = false
                    }
                )
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
        AccountSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            languageDropDownExpanded = false,
            themeDropDownExpanded = false,
            accountSettingsData = AccountSettingsData(),
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            ),
            showDeleteAccountDialog = false,
            firstStartUp = true
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentDarkPreview() {
    VoiceCallTranslatorPreview {
        AccountSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            languageDropDownExpanded = false,
            themeDropDownExpanded = false,
            accountSettingsData = Dummy.accountSettingsDataDark,
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            ),
            showDeleteAccountDialog = false,
            firstStartUp = false
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentLightPreview() {
    VoiceCallTranslatorPreview {
        AccountSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            languageDropDownExpanded = false,
            themeDropDownExpanded = false,
            accountSettingsData = Dummy.accountSettingsDataLight,
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            ),
            showDeleteAccountDialog = false,
            firstStartUp = false
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentDropDownExpandedPreview() {
    VoiceCallTranslatorPreview {
        AccountSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            languageDropDownExpanded = true,
            themeDropDownExpanded = false,
            accountSettingsData = AccountSettingsData(),
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            ),
            showDeleteAccountDialog = false,
            firstStartUp = false
        )
    }
}

@PreviewLightDark
@Composable
fun VoiceSettingsScreenContentDeleteAccountDialogPreview() {
    VoiceCallTranslatorPreview {
        AccountSettingsScreenContent(
            modifier = Modifier,
            navigatePopBackStack = {},
            languageDropDownExpanded = false,
            themeDropDownExpanded = false,
            accountSettingsData = AccountSettingsData(),
            accountSettingsAction = AccountSettingsActions(
                setLanguage = {},
                setTheme = {},
                logout = {},
                deleteAccount = {}
            ),
            showDeleteAccountDialog = true,
            firstStartUp = false
        )
    }
}
