package com.sergiolopez.voicecalltranslator.feature.splash.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    //private val webRtcManager: WebRtcManager,
    //private val mainRepository: MainRepository
) : VoiceCallTranslatorViewModel() {

    fun onAppStart(openAndPopUp: (NavigationParams) -> Unit) {
        if (firebaseAuthRepository.isUserLogged()) {
            launchCatching {
                /*initWebrtcClient(
                    // TODO : I don't like this!
                    user = firebaseAuthService.currentUser.first() as User.UserData
                )*/
            }
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

    /*private fun initWebrtcClient(user: User.UserData) {
        mainRepository.initWebrtcClient(
            username = user.id
        )
    }*/
}
