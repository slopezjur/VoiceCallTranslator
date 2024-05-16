package com.sergiolopez.voicecalltranslator.feature.splash.ui

import android.content.Context
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.utils.LocaleProvider
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.LanguageOption
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import com.sergiolopez.voicecalltranslator.feature.splash.domain.usecase.GetLanguageOptionUseCase
import com.sergiolopez.voicecalltranslator.feature.splash.domain.usecase.GetThemeOptionUseCase
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getThemeOptionUseCase: GetThemeOptionUseCase,
    private val getLanguageOptionUseCase: GetLanguageOptionUseCase
) : VoiceCallTranslatorViewModel() {

    fun onAppStart(
        openAndPopUp: (NavigationParams) -> Unit,
        themeConfiguration: (ThemeOption) -> Unit
    ) {
        if (firebaseAuthRepository.isUserLogged()) {
            launchCatching {
                val userId = firebaseAuthRepository.currentUser.value?.id

                userId?.let {
                    setUpLanguage(
                        userId = it
                    )
                    setUpTheme(
                        userId = it,
                        themeConfiguration = themeConfiguration
                    )
                }

                openAndPopUp(
                    NavigationParams(
                        route = NavigationRoute.CONTACT_LIST.navigationName,
                        popUp = NavigationRoute.SPLASH.navigationName
                    )
                )
            }
        } else {
            openAndPopUp(
                NavigationParams(
                    route = NavigationRoute.LOGIN.navigationName,
                    popUp = NavigationRoute.SPLASH.navigationName
                )
            )
        }
    }

    private suspend fun setUpLanguage(userId: String) {
        val languageOption = getLanguageOptionUseCase.invoke(
            userId = userId
        )?.getLocalValue()

        languageOption?.let {
            LocaleProvider.updateLanguage(context, it)
        } ?: LocaleProvider.updateLanguage(context, LanguageOption.ENGLISH.getLocalValue())
    }

    private suspend fun setUpTheme(userId: String, themeConfiguration: (ThemeOption) -> Unit) {
        // TODO : Who should manage the default values?
        val themeOption = getThemeOptionUseCase.invoke(
            userId = userId
        ) ?: ThemeOption.SYSTEM

        themeConfiguration.invoke(themeOption)
    }
}
