package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import com.sergiolopez.voicecalltranslator.R

enum class ThemeOption(val nameValue: Int) {
    SYSTEM(R.string.system),
    DARK(R.string.dark),
    LIGHT(R.string.light);

    companion object {
        fun getThemeEnum(text: String): ThemeOption {
            return when (text.lowercase()) {
                SYSTEM.name.lowercase() -> {
                    SYSTEM
                }

                DARK.name.lowercase() -> {
                    DARK
                }

                LIGHT.name.lowercase() -> {
                    LIGHT
                }

                else -> {
                    SYSTEM
                }
            }
        }
    }
}