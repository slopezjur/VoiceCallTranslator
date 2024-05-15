package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import android.content.Context
import com.sergiolopez.voicecalltranslator.R

enum class LanguageOption(val nameValue: Int) {
    ENGLISH(R.string.english),
    SPANISH(R.string.spanish);

    companion object {
        fun getLanguageEnum(context: Context, text: String): LanguageOption {
            return when (text.lowercase()) {
                context.getString(ENGLISH.nameValue).lowercase() -> {
                    ENGLISH
                }

                context.getString(SPANISH.nameValue).lowercase() -> {
                    SPANISH
                }

                else -> {
                    ENGLISH
                }
            }
        }
    }
}