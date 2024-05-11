package com.sergiolopez.voicecalltranslator.feature.settings.account.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferencesScope = CoroutineScope(SupervisorJob())

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFS_ACCOUNT_SETTINGS,
        corruptionHandler = ReplaceFileCorruptionHandler { preferencesOf() },
        scope = preferencesScope
    )

    suspend fun setAccountSettings(userId: String, accountSettingsData: AccountSettingsData) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey(userId)] =
                Json.encodeToString(AccountSettingsData.serializer(), accountSettingsData)
        }
    }

    suspend fun getAccountSettings(userId: String): AccountSettingsData? {
        return context.dataStore.data.mapNotNull { prefs ->
            prefs[stringPreferencesKey(userId)]?.let {
                Json.decodeFromString<AccountSettingsData>(it)
            }
        }.firstOrNull()
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        preferencesScope.cancel()
    }

    companion object {
        private const val PREFS_ACCOUNT_SETTINGS = "prefs_account_settings"
    }
}