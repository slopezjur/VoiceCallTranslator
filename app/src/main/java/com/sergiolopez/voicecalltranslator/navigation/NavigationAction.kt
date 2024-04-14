package com.sergiolopez.voicecalltranslator.navigation

sealed class NavigationAction(val route: String) {
    data object Login : NavigationAction(NavigationRoute.LOGIN.navigationName)
    data object SignUp : NavigationAction(NavigationRoute.SIGN_UP.navigationName)
    data object ContactList : NavigationAction(NavigationRoute.CONTACT_LIST.navigationName)
    data object VoiceSettings : NavigationAction(NavigationRoute.VOICE_SETTINGS.navigationName)
    data object AccountSettings : NavigationAction(NavigationRoute.ACCOUNT_SETTINGS.navigationName)
    data object VoiceTraining : NavigationAction(NavigationRoute.VOICE_TRAINING.navigationName)
}