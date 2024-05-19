package com.sergiolopez.voicecalltranslator.navigation

import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption

data class NavigationAccountSettings(
    val navigatePopBackStack: () -> Unit,
    val setThemeConfiguration: (ThemeOption) -> Unit,
    val navigateAndPopUp: (NavigationParams) -> Unit
)