package com.sergiolopez.voicecalltranslator.feature.login.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.login.domain.usecase.LoginUseCase
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : VoiceCallTranslatorViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState.CONTINUE)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _emailState = MutableStateFlow("")
    val emailState: StateFlow<String> = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState: StateFlow<String> = _passwordState.asStateFlow()

    fun resetUiState() {
        _loginUiState.value = LoginUiState.CONTINUE
    }

    fun updateEmail(newEmail: String) {
        _emailState.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _passwordState.value = newPassword
    }

    fun onLoginClick(openAndPopUp: (NavigationParams) -> Unit) {
        _loginUiState.value = LoginUiState.LOADING
        launchCatching {
            firebaseAuthRepository.currentUser.collect { user ->
                if (user is User) {
                    saveUserUseCase.invoke(user)
                    navigateToNextScreen(openAndPopUp)
                }
            }
        }
        launchCatching {
            val loginResult = loginUseCase.invoke(_emailState.value, _passwordState.value)
            if (!loginResult) {
                _loginUiState.value = LoginUiState.ERROR
            }
        }
    }

    private fun navigateToNextScreen(openAndPopUp: (NavigationParams) -> Unit) {
        openAndPopUp(
            NavigationParams(
                NavigationRoute.CONTACT_LIST.navigationName,
                NavigationRoute.LOGIN.navigationName
            )
        )
    }

    fun onSignUpClick(openAndPopUp: (NavigationParams) -> Unit) {
        openAndPopUp(
            NavigationParams(
                NavigationRoute.SIGN_UP.navigationName,
                NavigationRoute.LOGIN.navigationName
            )
        )
    }

    enum class LoginUiState {
        LOADING,
        CONTINUE,
        ERROR
    }
}