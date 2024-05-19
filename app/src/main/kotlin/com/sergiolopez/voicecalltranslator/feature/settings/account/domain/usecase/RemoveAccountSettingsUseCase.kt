package com.sergiolopez.voicecalltranslator.feature.settings.account.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore.AccountSettingsDataStore
import javax.inject.Inject

class RemoveAccountSettingsUseCase @Inject constructor(
    private val accountSettingsDataStore: AccountSettingsDataStore
) {
    suspend fun invoke(userId: String) {
        return accountSettingsDataStore.remove(
            userId = userId
        )
    }
}