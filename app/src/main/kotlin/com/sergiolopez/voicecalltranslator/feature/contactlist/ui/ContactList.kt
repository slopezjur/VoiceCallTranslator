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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme

@Composable
fun ContactListScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    contactListViewModel: ContactListViewModel = hiltViewModel()
) {
    val contactList = contactListViewModel.userList.collectAsState().value

    ContactListContent(
        openAndPopUp = openAndPopUp,
        contactList = contactList
    )
}

@Composable
fun ContactListContent(
    openAndPopUp: (NavigationParams) -> Unit,
    contactList: List<User>
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Settings, "Settings")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                LazyColumn {
                    items(contactList, key = { it.id }) { contactItem ->
                        Spacer(modifier = Modifier.size(8.dp))

                        ContactItem(
                            user = contactItem,
                            onContactUserClick = {}
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ContactListScreenPreview() {
    VoiceCallTranslatorTheme {
        Surface {
            ContactListContent(
                openAndPopUp = {},
                contactList = Dummy.userList,
            )
        }
    }
}