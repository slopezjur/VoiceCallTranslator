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
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.MyPeerObserver
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.permissions.PermissionBox
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import javax.inject.Inject

@AndroidEntryPoint
class VoiceCallTranslatorActivity : ComponentActivity() {

    @Inject
    lateinit var saveUserUseCase: SaveUserUseCase

    @Inject
    lateinit var webRTCClient: WebRTCClient

    private val target = "test2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCallActivity()

        setUpWebRtc(webRTCClient = webRTCClient)

        setContent {
            VoiceCallTranslatorTheme {
                Surface {
                    PermissionBox(permissions = setUpPermissions()) {
                        VoiceCallTranslatorApp()
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

    private fun setUpWebRtc(webRTCClient: WebRTCClient) {
        webRTCClient.initializeWebrtcClient("username", object : MyPeerObserver() {

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                try {
                    //p0?.audioTracks?.get(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRTCClient.sendIceCandidate(target, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                super.onConnectionChange(newState)
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    TODO("Not yet implemented")
                    // 1. change my status to in call
                    //firebaseClient.changeMyStatus(UserStatus.IN_CALL)
                    // 2. clear latest event inside my user section in firebase database
                    //firebaseClient.clearLatestEvent()
                }
            }
        })
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
    }
}