package com.sergiolopez.voicecalltranslator.feature.login.ui

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
fun LoginScreen(
    navigateAndPopUp: (NavigationParams) -> Unit,
    navigate: (NavigationParams) -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    LoginScreenContent(
        navigateAndPopUp = navigateAndPopUp,
        navigate = navigate,
        loginUiState = loginViewModel.loginUiState.collectAsStateWithLifecycle().value,
        resetUiState = { loginViewModel.resetUiState() },
        email = loginViewModel.emailState.collectAsStateWithLifecycle().value,
        password = loginViewModel.passwordState.collectAsStateWithLifecycle().value,
        updateEmail = { loginViewModel.updateEmail(it) },
        updatePassword = { loginViewModel.updatePassword(it) },
        onLoginClick = { loginViewModel.onLoginClick(it) },
        onSignUpClick = { loginViewModel.onSignUpClick(it) }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    navigateAndPopUp: (NavigationParams) -> Unit,
    navigate: (NavigationParams) -> Unit,
    loginUiState: LoginViewModel.LoginUiState,
    resetUiState: () -> Unit,
    email: String,
    password: String,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    onLoginClick: ((NavigationParams) -> Unit) -> Unit,
    onSignUpClick: ((NavigationParams) -> Unit) -> Unit
) {
    SimpleSnackbarScaffold { paddingValues, showSnackbar ->
        when (loginUiState) {
            LoginViewModel.LoginUiState.LOADING -> {
                LoadingView(modifier = modifier)
            }

            LoginViewModel.LoginUiState.CONTINUE -> {
                ShowLoginScreenContent(
                    modifier = modifier,
                    paddingValues = paddingValues,
                    navigateAndPopUp = navigateAndPopUp,
                    navigate = navigate,
                    email = email,
                    updateEmail = updateEmail,
                    password = password,
                    updatePassword = updatePassword,
                    onLoginClick = onLoginClick,
                    onSignUpClick = onSignUpClick
                )
            }

            LoginViewModel.LoginUiState.ERROR -> {
                showSnackbar.invoke("Server error. Bad email format, password with less than 6 characters, general error, etc...")
                resetUiState.invoke()
            }
        }
    }
}

@PreviewLightDark
@Composable
fun LoginScreenLoadingPreview() {
    VoiceCallTranslatorPreview {
        LoginScreenContent(
            modifier = Modifier,
            navigateAndPopUp = {},
            navigate = {},
            loginUiState = LoginViewModel.LoginUiState.LOADING,
            resetUiState = {},
            email = "",
            password = "",
            updateEmail = {},
            updatePassword = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}

@PreviewLightDark
@Composable
fun LoginScreenErrorPreview() {
    VoiceCallTranslatorPreview {
        LoginScreenContent(
            modifier = Modifier,
            navigateAndPopUp = {},
            navigate = {},
            loginUiState = LoginViewModel.LoginUiState.ERROR,
            resetUiState = {},
            email = "slopezjur@uoc.edu",
            password = "SUPERCOMPLEXPASSWORD",
            updateEmail = {},
            updatePassword = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}