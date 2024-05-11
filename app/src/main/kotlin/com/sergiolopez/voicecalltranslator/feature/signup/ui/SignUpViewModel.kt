package com.sergiolopez.voicecalltranslator.feature.signup.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
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
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val saveUserUseCase: SaveUserUseCase
) : VoiceCallTranslatorViewModel() {

    private val _signUpUiState = MutableStateFlow(SignUpUiState.CONTINUE)
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState

    private val _emailState = MutableStateFlow("")
    val emailState: StateFlow<String> = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState: StateFlow<String> = _passwordState

    private val _confirmPasswordState = MutableStateFlow("")
    val confirmPasswordState: StateFlow<String> = _confirmPasswordState

    private val _isPasswordError = MutableStateFlow(false)
    val isPasswordDifferent: StateFlow<Boolean> = _isPasswordError

    fun resetUiState() {
        _signUpUiState.value = SignUpUiState.CONTINUE
    }

    fun updateEmail(newEmail: String) {
        _emailState.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _passwordState.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPasswordState.value = newConfirmPassword
    }

    fun resetPasswordDifferent() {
        _isPasswordError.value = false
    }

    fun onSignUpClick(openAndPopUp: (NavigationParams) -> Unit) {
        if (_passwordState.value != _confirmPasswordState.value) {
            _isPasswordError.value = true
        } else {
            _signUpUiState.value = SignUpUiState.LOADING
            launchCatching {
                firebaseAuthRepository.currentUser.collect { user ->
                    if (user is User) {
                        val signUpResult = saveUserUseCase.invoke(user)
                        if (!signUpResult) {
                            _signUpUiState.value = SignUpUiState.ERROR
                            // TODO : Remove Firebase user if error for the User replica
                        } else {
                            navigateToNextScreen(openAndPopUp)
                        }
                    }
                }
            }
            launchCatching {
                val signUpResult = signUpUseCase.invoke(_emailState.value, _passwordState.value)
                if (!signUpResult) {
                    _signUpUiState.value = SignUpUiState.ERROR
                }
            }
        }
    }

    private fun navigateToNextScreen(openAndPopUp: (NavigationParams) -> Unit) {
        openAndPopUp.invoke(
            NavigationParams(
                NavigationRoute.CONTACT_LIST.navigationName,
                NavigationRoute.LOGIN.navigationName
            )
        )
    }

    enum class SignUpUiState {
        LOADING,
        CONTINUE,
        ERROR
    }
}