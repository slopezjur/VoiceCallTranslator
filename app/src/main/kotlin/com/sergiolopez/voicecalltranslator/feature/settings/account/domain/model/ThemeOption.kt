package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import android.content.Context
import com.sergiolopez.voicecalltranslator.R

enum class ThemeOption(val nameValue: Int) {
    SYSTEM(R.string.system),
    DARK(R.string.dark),
    LIGHT(R.string.light);

    companion object {
        fun getThemeEnum(context: Context, text: String): ThemeOption {
            return when (text.lowercase()) {
                context.getString(SYSTEM.nameValue).lowercase() -> {
                    SYSTEM
                }

                context.getString(DARK.nameValue).lowercase() -> {
                    DARK
                }

                context.getString(LIGHT.nameValue).lowercase() -> {
                    LIGHT
                }

                else -> {
                    SYSTEM
                }
            }
        }
    }
}