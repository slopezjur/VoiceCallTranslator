package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model

import android.content.Context
import com.sergiolopez.voicecalltranslator.R

enum class LanguageOption(val nameValue: Int) {
    ENGLISH(R.string.english),
    SPANISH(R.string.spanish);

    fun getLocalValue(): String {
        return when (this) {
            ENGLISH -> LOCALE_EN
            SPANISH -> LOCALE_ES
        }
    }

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

        private const val LOCALE_ES = "es"
        private const val LOCALE_EN = "en"
    }
}