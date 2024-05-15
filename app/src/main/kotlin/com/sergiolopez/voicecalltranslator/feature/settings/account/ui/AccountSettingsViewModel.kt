package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore.AccountSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.LanguageOption
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val accountSettingsDataStore: AccountSettingsDataStore
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
                accountSettingsDataStore.getAccountSettings(
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
    }

    fun setTheme(theme: ThemeOption) {
        _accountSettingsDataStateState.value = _accountSettingsDataStateState.value.copy(
            themeOption = theme
        )
        setAccountSettings()
    }

    fun logout() {
        launchCatching {
            val result = firebaseAuthRepository.logout()
            if (result) {
                // Remove data store and go to login
            }
        }
    }

    fun deleteAccount() {
        launchCatching {
            val result = firebaseAuthRepository.deleteAccount()
            if (result) {
                // Remove data store and go to login
            }
        }
    }

    private fun setAccountSettings() {
        launchCatching {
            user?.id?.let { userId ->
                accountSettingsDataStore.setAccountSettings(
                    userId = userId,
                    accountSettingsData = accountSettingsDataState.value
                )
            }
        }
    }
}
