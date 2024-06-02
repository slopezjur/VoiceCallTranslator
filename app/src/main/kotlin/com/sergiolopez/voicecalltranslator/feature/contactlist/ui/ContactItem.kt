package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    user: User,
    onContactUserClick: (Contact) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = {
            onContactUserClick.invoke(
                Contact(
                    id = user.id,
                    email = user.email
                )
            )
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Icon(
                modifier = modifier
                    .padding(16.dp)
                    .size(36.dp),
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Contact icon"
            )
            Text(
                text = user.email,
                modifier = modifier
                    .padding(8.dp)
                    .weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis

            )
            Icon(
                modifier = modifier
                    .padding(16.dp),
                imageVector = Icons.Rounded.Call,
                contentDescription = "Contact icon"
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ContactItemPreview() {
    VoiceCallTranslatorPreview {
        ContactItem(
            user = Dummy.user,
            onContactUserClick = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ContactItemVeryLongEmailPreview() {
    VoiceCallTranslatorPreview {
        ContactItem(
            user = Dummy.user.copy(
                email = "This is a very very long long email email very long email"
            ),
            onContactUserClick = {}
        )
    }
}
