package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

import android.content.Context
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.LanguageOption
import com.sergiolopez.voicecalltranslator.feature.common.domain.usecase.GetAccountSettingsUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.usecase.SetAccountSettingsUseCase
import com.sergiolopez.voicecalltranslator.feature.common.utils.LocaleProvider
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.usecase.RemoveAccountSettingsUseCase
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val getAccountSettingsUseCase: GetAccountSettingsUseCase,
    private val setAccountSettingsUseCase: SetAccountSettingsUseCase,
    private val removeAccountSettingsUseCase: RemoveAccountSettingsUseCase
) : VoiceCallTranslatorViewModel() {

    private val _accountSettingsDataStateState = MutableStateFlow(AccountSettingsData())
    val accountSettingsDataState: StateFlow<AccountSettingsData>
        get() = _accountSettingsDataStateState.asStateFlow()

    private val user = firebaseAuthRepository.currentUser.value

    init {
        loadAccountSettings()
    }

    private fun loadAccountSettings() {
        launchCatching {
            user?.let { user ->
                getAccountSettingsUseCase.invoke(
                    userId = user.id
                )?.let {
                    _accountSettingsDataStateState.value = it
                }
            }
        }
    }

    fun setLanguage(languageOption: LanguageOption) {
        _accountSettingsDataStateState.value = _accountSettingsDataStateState.value.copy(
            languageOption = languageOption
        )
        setAccountSettings()
        LocaleProvider.updateLanguage(context, languageOption.getLocalValue())
    }

    fun setTheme(theme: ThemeOption) {
        _accountSettingsDataStateState.value = _accountSettingsDataStateState.value.copy(
            themeOption = theme
        )
        setAccountSettings()
    }

    fun logout(
        navigateAndPopUp: (NavigationParams) -> Unit
    ) {
        launchCatching {
            val result = firebaseAuthRepository.logout()
            if (result) {
                navigateAndPopUp(
                    NavigationParams(
                        route = NavigationRoute.LOGIN.navigationName,
                        popUp = NavigationRoute.ACCOUNT_SETTINGS.navigationName
                    )
                )
            }
        }
    }

    fun deleteAccount(
        navigateAndPopUp: (NavigationParams) -> Unit
    ) {
        // TODO : Implement loading
        launchCatching {
            user?.let { user ->
                val result = firebaseDatabaseRepository.removeUser(
                    userId = user.id
                )

                if (result) {
                    // NOTE : Implement only one call to remove User from both sources
                    firebaseAuthRepository.deleteAccount()
                    removeAccountSettingsUseCase.invoke(
                        userId = user.id
                    )
                }
            }

            navigateAndPopUp(
                NavigationParams(
                    route = NavigationRoute.LOGIN.navigationName,
                    popUp = NavigationRoute.ACCOUNT_SETTINGS.navigationName
                )
            )
        }
    }

    private fun setAccountSettings() {
        launchCatching {
            user?.id?.let { userId ->
                setAccountSettingsUseCase.invoke(
                    userId = userId,
                    accountSettingsData = accountSettingsDataState.value
                )
            }
        }
    }
}
