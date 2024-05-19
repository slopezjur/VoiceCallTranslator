package com.sergiolopez.voicecalltranslator.feature.signup.ui

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
internal fun ShowSignUpScreenContent(
    modifier: Modifier,
    paddingValues: PaddingValues,
    clearAndNavigate: (NavigationParams) -> Unit,
    email: String,
    updateEmail: (String) -> Unit,
    password: String,
    updatePassword: (String) -> Unit,
    confirmPassword: String,
    updateConfirmPassword: (String) -> Unit,
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
                unfocusedIndicatorColor = Color.Transparent,
            ),
            value = password,
            onValueChange = { updatePassword(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
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
            value = confirmPassword,
            onValueChange = { updateConfirmPassword(it) },
            placeholder = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Button(
            onClick = {
                onSignUpClick(clearAndNavigate)
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 16.sp,
                modifier = modifier.padding(0.dp, 6.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ShowSignUpScreenContentPreview() {
    VoiceCallTranslatorPreview {
        ShowSignUpScreenContent(
            modifier = Modifier,
            paddingValues = PaddingValues(),
            clearAndNavigate = {},
            email = "",
            password = "",
            confirmPassword = "",
            updateEmail = {},
            updatePassword = {},
            updateConfirmPassword = {},
            onSignUpClick = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ShowSignUpScreenContentFilledPreview() {
    VoiceCallTranslatorPreview {
        ShowSignUpScreenContent(
            modifier = Modifier,
            paddingValues = PaddingValues(),
            clearAndNavigate = {},
            email = "slopezjur@uoc.edu",
            password = "SUPERCOMPLEXPASSWORD",
            confirmPassword = "SUPERCOMPLEXPASSWORD",
            updateEmail = {},
            updatePassword = {},
            updateConfirmPassword = {},
            onSignUpClick = {}
        )
    }
}