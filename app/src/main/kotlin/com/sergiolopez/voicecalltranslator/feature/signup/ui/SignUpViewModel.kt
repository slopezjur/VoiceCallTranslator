package com.sergiolopez.voicecalltranslator.feature.signup.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase.SignUpUseCase
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val firebaseAuthService: FirebaseAuthService,
    private val saveUserUseCase: SaveUserUseCase
) : VoiceCallTranslatorViewModel() {

    private val _emailState = MutableStateFlow("")
    val emailState: StateFlow<String> = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState: StateFlow<String> = _passwordState

    private val _confirmPasswordState = MutableStateFlow("")
    val confirmPasswordState: StateFlow<String> = _confirmPasswordState

    private val _isPasswordError = MutableStateFlow(false)
    val isPasswordDifferent: StateFlow<Boolean> = _isPasswordError

    private val _signUpUiState = MutableStateFlow(SignUpUiState.LOADING)
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState

    fun updateEmail(newEmail: String) {
        _emailState.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _passwordState.value = newPassword
        resetPasswordDifferent()
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPasswordState.value = newConfirmPassword
        resetPasswordDifferent()
    }

    private fun resetPasswordDifferent() {
        _isPasswordError.value = false
    }

    fun onSignUpClick(openAndPopUp: (NavigationParams) -> Unit) {
        if (_passwordState.value != _confirmPasswordState.value) {
            _isPasswordError.value = true
        } else {
            launchCatching {
                firebaseAuthService.currentUser.collect { user ->
                    if (user is User.UserData) {
                        // TODO: What happen if this sync fails with the Database?
                        saveUserUseCase.invoke(user)
                        _signUpUiState.value = SignUpUiState.LOGGED
                        openAndPopUp.invoke(
                            NavigationParams(
                                NavigationRoute.CONTACT_LIST.navigationName,
                                NavigationRoute.LOGIN.navigationName
                            )
                        )
                    }
                }
            }
            launchCatching {
                signUpUseCase.invoke(_emailState.value, _passwordState.value)
            }
        }
    }

    enum class SignUpUiState {
        LOADING,
        LOGGED,
        CONTINUE,
        ERROR
    }
}