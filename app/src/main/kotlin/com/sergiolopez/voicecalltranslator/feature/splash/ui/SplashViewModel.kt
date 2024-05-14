package com.sergiolopez.voicecalltranslator.feature.splash.ui

import android.content.Context
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.utils.LocaleProvider
import com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore.AccountSettingsDataStore
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val accountSettingsDataStore: AccountSettingsDataStore
) : VoiceCallTranslatorViewModel() {

    fun onAppStart(
        openAndPopUp: (NavigationParams) -> Unit
    ) {
        if (firebaseAuthRepository.isUserLogged()) {
            launchCatching {
                /*val accountSettings = accountSettingsDataStore.getAccountSettings(
                    userId = firebaseAuthRepository.currentUser.value?.id ?: ""
                )*/
                if (context.resources.configuration.locales[0].language != "es") {
                    //setLocaleAndRestart.invoke(context, "es")//accountSettings?.language ?: "en")
                    LocaleProvider.updateLanguage(context, "es")
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
}
