package com.sergiolopez.voicecalltranslator

import android.Manifest
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.content.getSystemService
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseService
import com.sergiolopez.voicecalltranslator.navigation.NavigationCallExtra
import com.sergiolopez.voicecalltranslator.permissions.PermissionBox
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceCallTranslatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCallActivity()

        var navigationCallExtra = initNavigationCallExtra()

        intent.extras?.let {
            val result = intent.getParcelableExtra<Call.CallData>(CALL_DATA_FROM_NOTIFICATION)
            if (result != null) {
                navigationCallExtra = NavigationCallExtra(
                    call = result,
                    hasCallData = true,
                )
            }
        }

        setContent {
            VoiceCallTranslatorTheme {
                Surface {
                    PermissionBox(permissions = setUpPermissions()) {
                        VoiceCallTranslatorApp(
                            navigationCallExtra = navigationCallExtra,
                            restartFirebaseService = {
                                Log.d("VCT_LOGS restartFirebaseService: ", "restartFirebaseService")
                                startFirebaseService.invoke()
                            }
                        )
                    }
                }
            }
        }

        if (!navigationCallExtra.hasCallData) {
            startFirebaseService.invoke()
        }
    }

    private fun setupCallActivity() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        getSystemService<KeyguardManager>()?.requestDismissKeyguard(this, null)
    }

    private fun initNavigationCallExtra() = NavigationCallExtra(
        call = Call.CallNoData,
        hasCallData = false,
    )

    private fun setUpPermissions(): MutableList<String> {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)

        permissions.add(Manifest.permission.MANAGE_OWN_CALLS)

        // To show call notifications we need permissions since Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions
    }

    private val startFirebaseService: () -> Unit = {
        startService(
            Intent(this, FirebaseService::class.java).apply {
                action = FirebaseService.ACTION_START_SERVICE
            }
        )
    }

    /*override fun onResume() {
        super.onResume()
        // Force the service to update in case something change like Mic permissions.
        startService(
            Intent(this, TelecomCallService::class.java).apply {
                action = TelecomCallService.ACTION_UPDATE_CALL
            },
        )
    }*/

    companion object {
        const val CALL_DATA_FROM_NOTIFICATION = "call_data_from_notification"
        const val APP_ALREADY_RUNNING = "APP_ALREADY_RUNNING"
    }
}