package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.R

@Composable
fun DeleteAccountDialog(
    modifier: Modifier,
    onDeleteAccount: () -> Unit,
    onDismissDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissDialog.invoke() },
        title = { Text(text = stringResource(id = R.string.delete_account)) },
        text = { Text(text = stringResource(id = R.string.delete_account_description)) },
        confirmButton = {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete icon",
                modifier = modifier
                    .padding(8.dp)
                    .clickable {
                        onDismissDialog.invoke()
                        onDeleteAccount.invoke()
                    }
            )
        },
        dismissButton = {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close icon",
                modifier = modifier
                    .padding(8.dp)
                    .clickable {
                        onDismissDialog.invoke()
                    }
            )
        },
    )
}