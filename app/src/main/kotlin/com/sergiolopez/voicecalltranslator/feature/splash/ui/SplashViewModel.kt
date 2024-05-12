package com.sergiolopez.voicecalltranslator.feature.splash.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : VoiceCallTranslatorViewModel() {

    fun onAppStart(openAndPopUp: (NavigationParams) -> Unit) {
        if (firebaseAuthRepository.isUserLogged()) {
            openAndPopUp(
                NavigationParams(
                    route = NavigationRoute.CONTACT_LIST.navigationName,
                    popUp = NavigationRoute.SPLASH.navigationName
                )
            )
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
