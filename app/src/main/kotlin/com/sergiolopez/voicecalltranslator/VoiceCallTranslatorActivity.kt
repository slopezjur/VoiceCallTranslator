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
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseService
import com.sergiolopez.voicecalltranslator.permissions.PermissionBox
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VoiceCallTranslatorActivity : ComponentActivity() {

    @Inject
    lateinit var saveUserUseCase: SaveUserUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCallActivity()

        var callFromNotification = false

        intent.extras?.let {
            if (it.getBoolean(CALL_FROM_NOTIFICATION)) {
                callFromNotification = true
            }
        }

        setContent {
            VoiceCallTranslatorTheme {
                Surface {
                    PermissionBox(permissions = setUpPermissions()) {
                        VoiceCallTranslatorApp(
                            callFromNotification = callFromNotification
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

    private fun setUpPermissions(): MutableList<String> {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)

        permissions.add(Manifest.permission.MANAGE_OWN_CALLS)

        // To show call notifications we need permissions since Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions
    }

    override fun onResume() {
        super.onResume()
        // Force the service to update in case something change like Mic permissions.
        startService(
            Intent(this, TelecomCallService::class.java).apply {
                action = TelecomCallService.ACTION_UPDATE_CALL
            },
        )
        startService(
            Intent(this, FirebaseService::class.java).apply {
                action = FirebaseService.ACTION_START_SERVICE
            }
        )
    }

    companion object {
        const val CALL_FROM_NOTIFICATION = "CALL_FROM_NOTIFICATION"
        const val APP_ALREADY_RUNNING = "APP_ALREADY_RUNNING"
    }
}