package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun ContactCallDialog(
    modifier: Modifier = Modifier,
    contactToCall: String,
    onContactUserCall: (String) -> Unit,
    onDismissDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissDialog.invoke() },
        title = { Text(text = stringResource(id = R.string.call_dialog_title, contactToCall)) },
        text = { Text(text = stringResource(id = R.string.call_dialog_subtitle)) },
        confirmButton = {
            Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = "Contact icon",
                modifier = modifier
                    .padding(8.dp)
                    .clickable {
                        onDismissDialog.invoke()
                        onContactUserCall.invoke(contactToCall)
                    }
            )
        },
        dismissButton = {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Contact icon",
                modifier = modifier
                    .padding(8.dp)
                    .clickable {
                        onDismissDialog.invoke()
                    }
            )
        },
    )
}

@PreviewLightDark
@Composable
fun ContactCallDialogPreview() {
    VoiceCallTranslatorPreview {
        ContactCallDialog(
            contactToCall = Dummy.user.email,
            onContactUserCall = {},
            onDismissDialog = {}
        )
    }
}