package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import com.sergiolopez.voicecalltranslator.feature.common.domain.model.LanguageOption

data class AccountSettingsActions(
    val setLanguage: (LanguageOption) -> Unit = {},
    val setTheme: (ThemeOption) -> Unit = {},
    val continueAction: () -> Unit = {},
    val logout: () -> Unit = {},
    val deleteAccount: () -> Unit = {}
)