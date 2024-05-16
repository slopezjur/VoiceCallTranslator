package com.sergiolopez.voicecalltranslator.feature.splash.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore.AccountSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.LanguageOption
import javax.inject.Inject

class GetLanguageOptionUseCase @Inject constructor(
    private val accountSettingsDataStore: AccountSettingsDataStore
) {
    suspend fun invoke(userId: String): LanguageOption? {
        return accountSettingsDataStore.getAccountSettings(
            userId = userId
        )?.languageOption
    }
}