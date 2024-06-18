package com.sergiolopez.voicecalltranslator.feature.common.domain.model

import android.content.Context
import com.sergiolopez.voicecalltranslator.R

enum class LanguageOption(val nameValue: Int) {
    ENGLISH(R.string.english),
    SPANISH(R.string.spanish),
    POLISH(R.string.polish);

    fun getLocalValue(): String {
        return when (this) {
            ENGLISH -> LOCALE_EN
            SPANISH -> LOCALE_ES
            POLISH -> LOCALE_PL
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

                context.getString(POLISH.nameValue).lowercase() -> {
                    POLISH
                }

                else -> {
                    ENGLISH
                }
            }
        }

        private const val LOCALE_ES = "es"
        private const val LOCALE_EN = "en"
        private const val LOCALE_PL = "pl"
    }
}