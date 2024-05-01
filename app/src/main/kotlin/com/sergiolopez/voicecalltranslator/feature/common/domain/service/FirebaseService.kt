package com.sergiolopez.voicecalltranslator.feature.common.domain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sergiolopez.voicecalltranslator.feature.call.domain.TelecomCallManager
import com.sergiolopez.voicecalltranslator.feature.call.domain.TelecomCallManager.Companion.launchIncomingCall
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModelType
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.MainRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : Service() {

    @Inject
    lateinit var getConnectionUpdateUseCase: GetConnectionUpdateUseCase

    @Inject
    lateinit var telecomCallManager: TelecomCallManager

    @Inject
    lateinit var firebaseAuthService: FirebaseAuthService

    @Inject
    lateinit var mainRepository: MainRepository

    companion object {
        internal const val ACTION_START_SERVICE = "start_service"
        internal const val ACTION_INCOMING_CALL = "incoming_call"
        internal const val ACTION_UPDATE_CALL = "update_call"
    }

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            firebaseAuthService.currentUser.collect { user ->
                user?.let {
                    when (intent?.action) {
                        ACTION_START_SERVICE -> startService(it)
                        ACTION_INCOMING_CALL -> manageCall(it)
                        ACTION_UPDATE_CALL -> manageCall(it)

                        else -> {
                            //throw IllegalArgumentException("Unknown action")
                        }
                    }
                }
            }
        }

        return START_STICKY
    }

    private fun startService(user: User) {
        mainRepository.initFirebase(
            userId = user.id,
            scope = scope
        )
        initWebrtcClient(user)
        manageCall(user)
    }

    private fun initWebrtcClient(user: User) {
        mainRepository.initWebrtcClient(user.id)
    }

    private fun manageCall(user: User) {
        scope.launch {
            val result = getConnectionUpdateUseCase.invoke(user.id)
            if (result.isSuccess) {
                result.getOrThrow().collect { call ->
                    when (call.type) {
                        DataModelType.StartAudioCall -> {
                            val callData = Call.CallData(
                                callerId = call.sender ?: "",
                                calleeId = call.target,
                                isIncoming = true,
                                callStatus = CallStatus.INCOMING_CALL,
                                offerData = call.toString(),
                                answerData = "",
                                timestamp = call.timeStamp
                            )
                            launchIncomingCall(
                                call = callData
                            )
                        }

                        DataModelType.EndCall -> {
                            initWebrtcClient(user)
                            telecomCallManager
                        }

                        else -> {
                            // DO NOTHING
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? = null

}