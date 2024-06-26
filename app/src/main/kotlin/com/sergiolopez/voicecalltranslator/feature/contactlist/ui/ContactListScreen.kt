package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
import com.sergiolopez.voicecalltranslator.feature.common.ui.components.VctTopAppBar
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_EMAIL
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_ID
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun ContactListScreen(
    navigateAndPopUp: (NavigationParams) -> Unit,
    contactListViewModel: ContactListViewModel = hiltViewModel()
) {
    ContactListContent(
        contactList = contactListViewModel.userList.collectAsStateWithLifecycle().value,
        onContactUserCall = {
            navigateAndPopUp.invoke(
                NavigationParams(
                    route = "${NavigationRoute.CALL.navigationName}?$CALLEE_ID=${it.id}&$CALLEE_EMAIL=${it.email}",
                    popUp = NavigationRoute.CONTACT_LIST.navigationName
                )
            )
        },
        showCallDialog = false,
        showSettingsDropDownMenu = false,
        onVoiceSettings = {
            navigateAndPopUp.invoke(
                NavigationParams(
                    route = NavigationRoute.VOICE_SETTINGS.navigationName,
                    popUp = NavigationRoute.CONTACT_LIST.navigationName
                )
            )
        },
        onAccountSettings = {
            navigateAndPopUp.invoke(
                NavigationParams(
                    route = NavigationRoute.ACCOUNT_SETTINGS.navigationName,
                    popUp = NavigationRoute.CONTACT_LIST.navigationName
                )
            )
        }
    )
}

@Composable
fun ContactListContent(
    modifier: Modifier = Modifier,
    contactList: List<User>,
    onContactUserCall: (Contact) -> Unit,
    showCallDialog: Boolean,
    showSettingsDropDownMenu: Boolean,
    onVoiceSettings: () -> Unit,
    onAccountSettings: () -> Unit
) {
    var contactToCall by remember { mutableStateOf<Contact?>(null) }

    var showCallDialogRemember by remember { mutableStateOf(showCallDialog) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        VctTopAppBar(
            modifier = modifier,
            titleName = R.string.contact_list,
            hasNavigation = false,
            hasAction = true,
            navigatePopBackStack = {},
            content = {
                SettingsDropDownMenu(
                    showSettingsDropDownMenu = showSettingsDropDownMenu,
                    onVoiceSettings = onVoiceSettings,
                    onAccountSettings = onAccountSettings
                )
            }
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                items(contactList, key = { it.email }) { contactItem ->
                    ContactItem(
                        user = contactItem,
                        onContactUserClick = {
                            contactToCall = it
                            showCallDialogRemember = true
                        }
                    )
                    Spacer(modifier = modifier.size(16.dp))
                }
            }
        }
    }

    if (showCallDialogRemember) {
        contactToCall?.let {
            ContactCallDialog(
                modifier = modifier,
                contact = it,
                onContactUserCall = onContactUserCall,
                onDismissDialog = { showCallDialogRemember = false }
            )
        }
    }
}

@Composable
private fun SettingsDropDownMenu(
    showSettingsDropDownMenu: Boolean,
    onVoiceSettings: () -> Unit,
    onAccountSettings: () -> Unit
) {
    var settingsDropDownMenuRemember by remember { mutableStateOf(showSettingsDropDownMenu) }

    Box {
        IconButton(
            onClick = {
                settingsDropDownMenuRemember = !settingsDropDownMenuRemember
            }
        ) {
            Icon(Icons.Filled.MoreVert, "Show settings Menu")
        }

        DropdownMenu(
            expanded = settingsDropDownMenuRemember,
            onDismissRequest = {
                settingsDropDownMenuRemember = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.voice_settings)
                    )
                },
                onClick = {
                    settingsDropDownMenuRemember = false
                    onVoiceSettings.invoke()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.account_settings)
                    )
                },
                onClick = {
                    settingsDropDownMenuRemember = false
                    onAccountSettings.invoke()
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ContactListScreenPreview() {
    VoiceCallTranslatorPreview {
        ContactListContent(
            contactList = Dummy.userList,
            onContactUserCall = {},
            showCallDialog = false,
            showSettingsDropDownMenu = false,
            onVoiceSettings = {},
            onAccountSettings = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ContactListScreenShowCallDialogPreview() {
    VoiceCallTranslatorPreview {
        ContactListContent(
            contactList = Dummy.userList,
            onContactUserCall = {},
            showCallDialog = true,
            showSettingsDropDownMenu = false,
            onVoiceSettings = {},
            onAccountSettings = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ContactListScreenSettingsDropDownMenuPreview() {
    VoiceCallTranslatorPreview {
        ContactListContent(
            contactList = Dummy.userList,
            onContactUserCall = {},
            showCallDialog = false,
            showSettingsDropDownMenu = true,
            onVoiceSettings = {},
            onAccountSettings = {}
        )
    }
}