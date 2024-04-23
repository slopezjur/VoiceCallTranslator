package com.sergiolopez.voicecalltranslator.feature.login.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
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
    private val firebaseAuthService: FirebaseAuthService
) : VoiceCallTranslatorViewModel() {

    private val _emailState = MutableStateFlow("")
    val emailState: StateFlow<String> = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState: StateFlow<String> = _passwordState

    private val _loginUiState = MutableStateFlow(LoginUiState.LOADING)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _emailState.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _passwordState.value = newPassword
    }

    fun onLoginClick(openAndPopUp: (NavigationParams) -> Unit) {
        launchCatching {
            firebaseAuthService.currentUser.collect { user ->
                if (user is User.UserData) {
                    saveUserUseCase.invoke(user)
                    setLoggedAnNavigate(openAndPopUp)
                }
            }
        }
        launchCatching {
            loginUseCase.invoke(_emailState.value, _passwordState.value)
        }
    }

    private fun setLoggedAnNavigate(openAndPopUp: (NavigationParams) -> Unit) {
        _loginUiState.value = LoginUiState.LOGGED
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
        LOGGED,
        CONTINUE,
        ERROR
    }
}