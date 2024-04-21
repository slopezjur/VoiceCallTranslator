package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun ContactItem(
    user: User,
    onContactUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = { onContactUserClick.invoke(user.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Contact icon",
                modifier = modifier.padding(8.dp)
            )
            Text(
                text = user.email,
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyLarge
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
