package com.sergiolopez.voicecalltranslator.feature.login.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
internal fun ShowLoginScreenContent(
    modifier: Modifier,
    paddingValues: PaddingValues,
    navigateAndPopUp: (NavigationParams) -> Unit,
    navigate: (NavigationParams) -> Unit,
    email: String,
    updateEmail: (String) -> Unit,
    password: String,
    updatePassword: (String) -> Unit,
    onLoginClick: ((NavigationParams) -> Unit) -> Unit,
    onSignUpClick: ((NavigationParams) -> Unit) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                paddingValues = paddingValues
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 80.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = email,
            onValueChange = { updateEmail(it) },
            placeholder = { Text(stringResource(R.string.email)) },
        )

        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = password,
            onValueChange = { updatePassword(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Button(
            onClick = {
                onLoginClick.invoke(navigateAndPopUp)
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            enabled = email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text(
                text = stringResource(R.string.login),
                fontSize = 16.sp,
                modifier = modifier.padding(0.dp, 6.dp)
            )
        }

        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        TextButton(onClick = {
            onSignUpClick.invoke(navigate)
        }) {
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 16.sp
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ShowLoginScreenContentPreview() {
    VoiceCallTranslatorPreview {
        ShowLoginScreenContent(
            modifier = Modifier,
            paddingValues = PaddingValues(),
            navigateAndPopUp = {},
            navigate = {},
            email = "",
            updateEmail = {},
            password = "",
            updatePassword = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ShowLoginScreenContentFilledPreview() {
    VoiceCallTranslatorPreview {
        ShowLoginScreenContent(
            modifier = Modifier,
            paddingValues = PaddingValues(),
            navigateAndPopUp = {},
            navigate = {},
            email = "slopezjur@uoc.edu",
            updateEmail = {},
            password = "SUPERCOMPLEXPASSWORD",
            updatePassword = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}