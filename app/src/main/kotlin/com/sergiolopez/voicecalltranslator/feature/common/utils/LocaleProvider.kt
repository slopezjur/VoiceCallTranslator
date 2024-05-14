package com.sergiolopez.voicecalltranslator.feature.common.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleProvider {

    // Note : Both options will flick the View to load. Not as smooth as it should be...
    fun updateLanguage(context: Context, language: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(
                LocaleManager::class.java
            ).applicationLocales = LocaleList(Locale.forLanguageTag(language))
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(
                    language
                )
            )
        }
    }
}