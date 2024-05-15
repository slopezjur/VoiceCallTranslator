package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountSettingsData(
    val languageOption: LanguageOption = LanguageOption.ENGLISH,
    val themeOption: ThemeOption = ThemeOption.SYSTEM
)