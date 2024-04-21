package com.sergiolopez.voicecalltranslator.navigation

sealed class NavigationAction(val route: String) {
    data object LoginNavigation : NavigationAction(NavigationRoute.LOGIN.navigationName)
    data object SignUpNavigation : NavigationAction(NavigationRoute.SIGN_UP.navigationName)
    data object ContactListNavigation :
        NavigationAction(NavigationRoute.CONTACT_LIST.navigationName)

    data object CallNavigation : NavigationAction(NavigationRoute.CALL.navigationName)
    data object VoiceSettingsNavigation :
        NavigationAction(NavigationRoute.VOICE_SETTINGS.navigationName)

    data object AccountSettingsNavigation :
        NavigationAction(NavigationRoute.ACCOUNT_SETTINGS.navigationName)

    data object VoiceTrainingNavigation :
        NavigationAction(NavigationRoute.VOICE_TRAINING.navigationName)
}