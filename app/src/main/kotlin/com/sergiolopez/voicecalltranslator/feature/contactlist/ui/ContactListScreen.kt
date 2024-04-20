package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun ContactListScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    contactListViewModel: ContactListViewModel = hiltViewModel()
) {
    val contactList = contactListViewModel.userList.collectAsState().value

    ContactListContent(
        contactList = contactList,
        onContactUserCall = {
            openAndPopUp.invoke(
                NavigationParams(
                    NavigationRoute.CALL.navigationName,
                    NavigationRoute.CONTACT_LIST.navigationName
                )
            )
        },
        showCallDialog = false
    )
}

@Composable
fun ContactListContent(
    modifier: Modifier = Modifier,
    contactList: List<User>,
    onContactUserCall: (String) -> Unit,
    showCallDialog: Boolean
) {
    var contactToCall by remember { mutableStateOf("") }

    var showCallDialogRemember by remember { mutableStateOf(showCallDialog) }

    val onContactUserClick: (String) -> Unit = {
        contactToCall = it
        showCallDialogRemember = true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Settings, "Settings")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                LazyColumn {
                    items(contactList, key = { it.email }) { contactItem ->
                        Spacer(modifier = modifier.size(8.dp))

                        ContactItem(
                            user = contactItem,
                            onContactUserClick = { onContactUserClick.invoke(it) }
                        )
                    }
                }
            }
        }
    }

    if (showCallDialogRemember) {
        ContactCallDialog(
            modifier = modifier,
            contactToCall = contactToCall,
            onContactUserCall = onContactUserCall,
            onDismissDialog = { showCallDialogRemember = false }
        )
    }
}

@PreviewLightDark
@Composable
fun ContactListScreenPreview() {
    VoiceCallTranslatorPreview {
        ContactListContent(
            contactList = Dummy.userList,
            onContactUserCall = {},
            showCallDialog = false
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
            showCallDialog = true
        )
    }
}