package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.navigation

import com.sergiolopez.voicecalltranslator.navigation.NavigationParams

data class NavigationVoiceSettings(
    val navigatePopBackStack: () -> Unit,
    val clearAndNavigate: (NavigationParams) -> Unit
)