package com.sergiolopez.voicecalltranslator.feature.settings.voice.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.VoiceSettingsData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferencesScope = CoroutineScope(SupervisorJob())

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFS_VOICE_SETTINGS,
        corruptionHandler = ReplaceFileCorruptionHandler { preferencesOf() },
        scope = preferencesScope
    )

    suspend fun setVoiceSettings(userId: String, voiceSettingsData: VoiceSettingsData) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey(userId)] =
                Json.encodeToString(VoiceSettingsData.serializer(), voiceSettingsData)
        }
    }

    suspend fun getVoiceSettings(userId: String): VoiceSettingsData? {
        return context.dataStore.data.mapNotNull { prefs ->
            prefs[stringPreferencesKey(userId)]?.let {
                Json.decodeFromString<VoiceSettingsData>(it)
            }
        }.firstOrNull()
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        preferencesScope.cancel()
    }

    suspend fun saveByteArray(byteArray: ByteArray) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey(WAV_FILE_TTS)] = Base64.getEncoder().encodeToString(byteArray)
        }
    }

    suspend fun getByteArray(): ByteArray? {
        return context.dataStore.data.mapNotNull { prefs ->
            Base64.getDecoder().decode(prefs[stringPreferencesKey(WAV_FILE_TTS)])
        }.firstOrNull()
    }

    companion object {
        private const val PREFS_VOICE_SETTINGS = "prefs_voice_settings"
        private const val WAV_FILE_TTS = "openAi"
        private const val PCM_FILE_TTS = "openAi-PCM"
    }
}