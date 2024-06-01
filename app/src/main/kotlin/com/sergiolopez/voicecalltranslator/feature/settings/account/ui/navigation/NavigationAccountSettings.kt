package com.sergiolopez.voicecalltranslator.feature.settings.account.ui.navigation

import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams

data class NavigationAccountSettings(
    val navigatePopBackStack: () -> Unit,
    val setThemeConfiguration: (ThemeOption) -> Unit,
    val clearAndNavigate: (NavigationParams) -> Unit
)