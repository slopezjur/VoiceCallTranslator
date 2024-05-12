package com.sergiolopez.voicecalltranslator.feature.signup.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergiolopez.voicecalltranslator.feature.common.ui.components.LoadingView
import com.sergiolopez.voicecalltranslator.feature.common.ui.components.SimpleSnackbarScaffold
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun SignUpScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    SignUpScreenContent(
        openAndPopUp = openAndPopUp,
        signUpUiState = signUpViewModel.signUpUiState.collectAsStateWithLifecycle().value,
        resetUiState = { signUpViewModel.resetUiState() },
        email = signUpViewModel.emailState.collectAsStateWithLifecycle().value,
        updateEmail = { signUpViewModel.updateEmail(it) },
        password = signUpViewModel.passwordState.collectAsStateWithLifecycle().value,
        updatePassword = { signUpViewModel.updatePassword(it) },
        confirmPassword = signUpViewModel.confirmPasswordState.collectAsStateWithLifecycle().value,
        updateConfirmPassword = { signUpViewModel.updateConfirmPassword(it) },
        isPasswordError = signUpViewModel.isPasswordError.collectAsStateWithLifecycle().value,
        resetPasswordDifferent = { signUpViewModel.resetPasswordDifferent() },
        onSignUpClick = { signUpViewModel.onSignUpClick(it) }
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: (NavigationParams) -> Unit,
    signUpUiState: SignUpViewModel.SignUpUiState,
    resetUiState: () -> Unit,
    email: String,
    updateEmail: (String) -> Unit,
    password: String,
    updatePassword: (String) -> Unit,
    confirmPassword: String,
    updateConfirmPassword: (String) -> Unit,
    isPasswordError: Boolean,
    resetPasswordDifferent: () -> Unit,
    onSignUpClick: ((NavigationParams) -> Unit) -> Unit
) {
    SimpleSnackbarScaffold { paddingValues, showSnackbar ->

        if (isPasswordError) {
            showSnackbar("Error, Confirmed password does not match the Password!")
            resetPasswordDifferent.invoke()
        }

        when (signUpUiState) {
            SignUpViewModel.SignUpUiState.LOADING -> {
                LoadingView(modifier = modifier)
            }

            SignUpViewModel.SignUpUiState.CONTINUE -> {
                ShowSignUpScreenContent(
                    modifier = modifier,
                    openAndPopUp = openAndPopUp,
                    paddingValues = paddingValues,
                    email = email,
                    updateEmail = updateEmail,
                    password = password,
                    updatePassword = updatePassword,
                    confirmPassword = confirmPassword,
                    updateConfirmPassword = updateConfirmPassword,
                    onSignUpClick = onSignUpClick
                )
            }

            SignUpViewModel.SignUpUiState.ERROR -> {
                showSnackbar.invoke("Server error. Bad email format, password with less than 6 characters, general error, etc...")
                resetUiState.invoke()
            }
        }
    }
}

@PreviewLightDark
@Composable
fun SignUpScreenPasswordErrorPreview() {
    VoiceCallTranslatorPreview {
        SignUpScreenContent(
            openAndPopUp = {},
            email = "slopezjur@uoc.edu",
            password = "SUPERCOMPLEXPASSWORD",
            confirmPassword = "differentPassword",
            updateEmail = {},
            updatePassword = {},
            updateConfirmPassword = {},
            isPasswordError = true,
            resetPasswordDifferent = {},
            onSignUpClick = {},
            signUpUiState = SignUpViewModel.SignUpUiState.CONTINUE,
            resetUiState = {}
        )
    }
}

@PreviewLightDark
@Composable
fun SignUpScreenLoadingPreview() {
    VoiceCallTranslatorPreview {
        SignUpScreenContent(
            openAndPopUp = {},
            email = "slopezjur@uoc.edu",
            password = "SUPERCOMPLEXPASSWORD",
            confirmPassword = "SUPERCOMPLEXPASSWORD",
            updateEmail = {},
            updatePassword = {},
            updateConfirmPassword = {},
            isPasswordError = false,
            resetPasswordDifferent = {},
            onSignUpClick = {},
            signUpUiState = SignUpViewModel.SignUpUiState.LOADING,
            resetUiState = {}
        )
    }
}

@PreviewLightDark
@Composable
fun SignUpScreenErrorPreview() {
    VoiceCallTranslatorPreview {
        SignUpScreenContent(
            openAndPopUp = {},
            email = "slopezjur@uoc.edu",
            password = "SUPERCOMPLEXPASSWORD",
            confirmPassword = "SUPERCOMPLEXPASSWORD",
            updateEmail = {},
            updatePassword = {},
            updateConfirmPassword = {},
            isPasswordError = false,
            resetPasswordDifferent = {},
            onSignUpClick = {},
            signUpUiState = SignUpViewModel.SignUpUiState.ERROR,
            resetUiState = {}
        )
    }
}