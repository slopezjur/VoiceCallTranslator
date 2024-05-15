package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

data class AccountSettingsActions(
    val setLanguage: (LanguageOption) -> Unit = {},
    val setTheme: (ThemeOption) -> Unit = {},
    val logout: () -> Unit = {},
    val deleteAccount: () -> Unit = {}
)