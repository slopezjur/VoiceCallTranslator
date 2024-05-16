package com.sergiolopez.voicecalltranslator.feature.splash.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore.AccountSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import javax.inject.Inject

class GetThemeOptionUseCase @Inject constructor(
    private val accountSettingsDataStore: AccountSettingsDataStore
) {
    suspend fun invoke(userId: String): ThemeOption? {
        return accountSettingsDataStore.getAccountSettings(
            userId = userId
        )?.themeOption
    }
}