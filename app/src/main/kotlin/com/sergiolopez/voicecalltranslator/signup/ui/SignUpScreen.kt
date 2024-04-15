package com.sergiolopez.voicecalltranslator.signup.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme

@Composable
fun SignUpScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    SignUpScreenContent(
        openAndPopUp = openAndPopUp,
        email = signUpViewModel.emailState.collectAsState().value,
        password = signUpViewModel.passwordState.collectAsState().value,
        confirmPassword = signUpViewModel.confirmPasswordState.collectAsState().value,
        updateEmail = { signUpViewModel.updateEmail(it) },
        updatePassword = { signUpViewModel.updatePassword(it) },
        updateConfirmPassword = { signUpViewModel.updateConfirmPassword(it) },
        isPasswordError = signUpViewModel.isPasswordDifferent.collectAsState().value,
        onSignUpClick = { signUpViewModel.onSignUpClick(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    openAndPopUp: (NavigationParams) -> Unit,
    email: String,
    password: String,
    confirmPassword: String,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    updateConfirmPassword: (String) -> Unit,
    isPasswordError: Boolean,
    onSignUpClick: ((NavigationParams) -> Unit) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 80.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = email,
            onValueChange = { updateEmail(it) },
            placeholder = { Text(stringResource(R.string.email)) },
        )

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = password,
            onValueChange = { updatePassword(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = confirmPassword,
            onValueChange = { updateConfirmPassword(it) },
            placeholder = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            isError = isPasswordError
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Button(
            onClick = {
                onSignUpClick(openAndPopUp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 16.sp,
                modifier = Modifier.padding(0.dp, 6.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun SignUpScreenPreview() {
    VoiceCallTranslatorTheme {
        Surface {
            SignUpScreenContent(
                openAndPopUp = {},
                email = "slopezjur@uoc.edu",
                password = "SUPERCOMPLEXPASSWORD",
                confirmPassword = "SUPERCOMPLEXPASSWORD",
                updateEmail = {},
                updatePassword = {},
                updateConfirmPassword = {},
                isPasswordError = false,
                onSignUpClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
fun SignUpScreenPasswordErrorPreview() {
    VoiceCallTranslatorTheme {
        Surface {
            SignUpScreenContent(
                openAndPopUp = {},
                email = "slopezjur@uoc.edu",
                password = "SUPERCOMPLEXPASSWORD",
                confirmPassword = "differentPassword",
                updateEmail = {},
                updatePassword = {},
                updateConfirmPassword = {},
                isPasswordError = true,
                onSignUpClick = {}
            )
        }
    }
}