package com.sergiolopez.voicecalltranslator.feature.splash.ui

import android.content.Context
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.LanguageOption
import com.sergiolopez.voicecalltranslator.feature.common.domain.usecase.CreateAccountSettingsUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.usecase.GetAccountSettingsUseCase
import com.sergiolopez.voicecalltranslator.feature.common.utils.LocaleProvider
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getAccountSettingsUseCase: GetAccountSettingsUseCase,
    private val createAccountSettingsUseCase: CreateAccountSettingsUseCase
) : VoiceCallTranslatorViewModel() {

    fun onAppStart(
        navigateAndPopUp: (NavigationParams) -> Unit,
        themeConfiguration: (ThemeOption) -> Unit
    ) {
        if (firebaseAuthRepository.isUserLogged()) {
            launchCatching {
                val userId = firebaseAuthRepository.currentUser.value?.id

                userId?.let {
                    val accountSettingsData = getAccountSettingsUseCase.invoke(it)

                    if (accountSettingsData == null) {
                        val newAccountSettingsData = createAccountSettingsUseCase.invoke(
                            userId = it
                        )
                        setLanguageAndTheme(
                            accountSettingsData = newAccountSettingsData,
                            themeConfiguration = themeConfiguration
                        )
                    } else {
                        setLanguageAndTheme(
                            accountSettingsData = accountSettingsData,
                            themeConfiguration = themeConfiguration
                        )
                    }
                }

                navigateAndPopUp(
                    NavigationParams(
                        route = NavigationRoute.CONTACT_LIST.navigationName,
                        popUp = NavigationRoute.SPLASH.navigationName
                    )
                )
            }
        } else {
            navigateAndPopUp(
                NavigationParams(
                    route = NavigationRoute.LOGIN.navigationName,
                    popUp = NavigationRoute.SPLASH.navigationName
                )
            )
        }
    }

    private fun setLanguageAndTheme(
        accountSettingsData: AccountSettingsData,
        themeConfiguration: (ThemeOption) -> Unit
    ) {
        setUpLanguage(
            languageOption = accountSettingsData.languageOption
        )
        setUpTheme(
            themeOption = accountSettingsData.themeOption,
            themeConfiguration = themeConfiguration
        )
    }

    private fun setUpLanguage(languageOption: LanguageOption) {
        languageOption.getLocalValue().let {
            LocaleProvider.updateLanguage(context, it)
        }
    }

    private fun setUpTheme(
        themeOption: ThemeOption,
        themeConfiguration: (ThemeOption) -> Unit,
    ) {
        // TODO : Who should manage the default values?
        themeConfiguration.invoke(themeOption)
    }
}
