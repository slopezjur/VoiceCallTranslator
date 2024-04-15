package com.sergiolopez.voicecalltranslator.login.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.login.domain.subscriber.CurrentUserSubscriber
import com.sergiolopez.voicecalltranslator.login.domain.usecase.LoginUseCase
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
    private val currentUserSubscriber: CurrentUserSubscriber
) : VoiceCallTranslatorViewModel() {

    init {
        observeCurrentUserState()
        subscribeCurrentUser()
    }

    private val _emailState = MutableStateFlow("")
    val emailState: StateFlow<String> = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState: StateFlow<String> = _passwordState

    private val _loginUiState = MutableStateFlow(LoginUiState.LOADING)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private fun observeCurrentUserState() {
        launchCatching {
            currentUserSubscriber.currentUserState.collect { user ->
                if (user != null) {
                    _loginUiState.value = LoginUiState.LOGGED
                }
            }
        }
    }

    private fun subscribeCurrentUser() {
        launchCatching {
            currentUserSubscriber.subscribe()
        }
    }

    fun updateEmail(newEmail: String) {
        _emailState.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _passwordState.value = newPassword
    }

    fun onLoginClick() {
        launchCatching {
            loginUseCase.invoke(_emailState.value, _passwordState.value)
        }
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
        LOGGED,
        CONTINUE,
        ERROR
    }
}