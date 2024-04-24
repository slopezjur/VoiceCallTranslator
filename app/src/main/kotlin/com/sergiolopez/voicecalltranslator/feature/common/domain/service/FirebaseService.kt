package com.sergiolopez.voicecalltranslator.feature.common.domain.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.sergiolopez.voicecalltranslator.feature.call.domain.TelecomCallManager
import com.sergiolopez.voicecalltranslator.feature.call.domain.TelecomCallManager.Companion.launchIncomingCall
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetIncomingCallsUseCase
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : Service() {

    @Inject
    lateinit var getIncomingCallsUseCase: GetIncomingCallsUseCase

    @Inject
    lateinit var telecomCallManager: TelecomCallManager

    @Inject
    lateinit var firebaseAuthService: FirebaseAuthService

    companion object {
        internal const val ACTION_INCOMING_CALL = "incoming_call"
        internal const val ACTION_UPDATE_CALL = "update_call"
    }


    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_INCOMING_CALL -> test(firebaseAuthService.currentUser.value)
            ACTION_UPDATE_CALL -> test(firebaseAuthService.currentUser.value)

            else -> {
                //throw IllegalArgumentException("Unknown action")
            }
        }

        return START_STICKY
    }

    private fun test(user: User) {
        scope.launch {
            if (user is User.UserData) {
                val result = getIncomingCallsUseCase.invoke(user.id)
                if (result.isSuccess) {
                    result.getOrNull()?.collect { calls ->
                        calls.firstOrNull()?.let { call ->
                            launchIncomingCall(
                                name = call.callerId,
                                uri = Uri.parse(call.offerData)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateServiceState(user: User) {
        TODO("Not yet implemented")
    }

    override fun onBind(p0: Intent?): IBinder? = null

}