package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountSettingsData(
    val language: String = "en",
    val theme: ThemeOption = ThemeOption.DARK
)