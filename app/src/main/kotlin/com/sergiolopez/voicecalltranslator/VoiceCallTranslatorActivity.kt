package com.sergiolopez.voicecalltranslator

import android.Manifest
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseService
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import com.sergiolopez.voicecalltranslator.navigation.NavigationCallExtra
import com.sergiolopez.voicecalltranslator.permissions.PermissionBox
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import java.io.File


@AndroidEntryPoint
class VoiceCallTranslatorActivity : AppCompatActivity() {
    // Note : For now we have to use AppCompatActivity instead of ComponentActivity to update the Locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCallActivity()

        var navigationCallExtra = initNavigationCallExtra()

        intent.extras?.let {
            val result = intent.getStringExtra(CALL_DATA_FROM_NOTIFICATION)
            if (result != null) {
                navigationCallExtra = NavigationCallExtra(
                    call = Json.decodeFromString(Call.CallData.serializer(), result),
                    hasCallData = true,
                )
            }
        }

        setContent {
            // TODO : To update the device Theme will overwrite User configuration...
            var themeOption by remember { mutableStateOf(ThemeOption.SYSTEM) }
            val themeConfiguration: (ThemeOption) -> Unit = {
                themeOption = it
            }

            VoiceCallTranslatorTheme(
                darkTheme = shouldUseDarkTheme(
                    themeOption = themeOption
                )
            ) {
                Surface {
                    PermissionBox(permissions = setUpPermissions()) {
                        VoiceCallTranslatorApp(
                            navigationCallExtra = navigationCallExtra,
                            restartFirebaseService = {
                                Log.d(
                                    "$VCT_LOGS restartFirebaseService: ",
                                    "restartFirebaseService"
                                )
                                startFirebaseService.invoke()
                            },
                            themeConfiguration = themeConfiguration
                        )
                    }
                }
            }
        }

        if (!navigationCallExtra.hasCallData) {
            startFirebaseService.invoke()
        }

        // TODO : Testing, auto clean recordings folder
        cleanAudioRecords()
    }

    // TODO : Testing, auto clean recordings folder
    private fun cleanAudioRecords() {
        deleteDirectoryContents(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
            } else {
                this.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            }
        )
    }

    // TODO : Testing, auto clean recordings folder
    private fun deleteDirectoryContents(dir: File?) {
        if (dir != null && dir.isDirectory) {
            val files: Array<out File>? = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        deleteDirectoryContents(file)
                    }
                    file.delete()
                }
            }
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

    companion object {
        const val CALL_DATA_FROM_NOTIFICATION = "call_data_from_notification"
        const val APP_ALREADY_RUNNING = "APP_ALREADY_RUNNING"
    }
}

@Composable
private fun shouldUseDarkTheme(
    themeOption: ThemeOption,
): Boolean = when (themeOption) {
    ThemeOption.SYSTEM -> isSystemInDarkTheme()
    ThemeOption.LIGHT -> false
    ThemeOption.DARK -> true
}