package com.sergiolopez.voicecalltranslator

import android.Manifest
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.content.getSystemService
import com.sergiolopez.voicecalltranslator.feature.call.telecom.repository.TelecomCallRepository
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.subscriber.CurrentUserSubscriber
import com.sergiolopez.voicecalltranslator.permissions.PermissionBox
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VoiceCallTranslatorActivity : ComponentActivity() {

    @Inject
    lateinit var saveUserUseCase: SaveUserUseCase

    @Inject
    lateinit var currentUserSubscriber: CurrentUserSubscriber

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val telecomCallRepository =
            TelecomCallRepository.instance ?: TelecomCallRepository.create(applicationContext)

        setupCallActivity()

        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)

        permissions.add(Manifest.permission.MANAGE_OWN_CALLS)

        // To show call notifications we need permissions since Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            VoiceCallTranslatorTheme {
                Surface {
                    PermissionBox(permissions = permissions) {
                        VoiceCallTranslatorApp(
                            telecomCallRepository = telecomCallRepository
                        )
                    }
                }
            }
        }
    }

    private fun setupCallActivity() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        getSystemService<KeyguardManager>()?.requestDismissKeyguard(this, null)
    }

    override fun onResume() {
        super.onResume()
        // Force the service to update in case something change like Mic permissions.
        startService(
            Intent(this, TelecomCallService::class.java).apply {
                action = TelecomCallService.ACTION_UPDATE_CALL
            },
        )
    }
}