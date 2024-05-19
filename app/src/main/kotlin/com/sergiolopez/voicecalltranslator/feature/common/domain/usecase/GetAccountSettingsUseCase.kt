package com.sergiolopez.voicecalltranslator.feature.common.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore.AccountSettingsDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import javax.inject.Inject

class GetAccountSettingsUseCase @Inject constructor(
    private val accountSettingsDataStore: AccountSettingsDataStore
) {
    suspend fun invoke(userId: String): AccountSettingsData? {
        return accountSettingsDataStore.getAccountSettings(
            userId = userId
        )
    }
}