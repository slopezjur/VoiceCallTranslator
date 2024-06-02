package com.sergiolopez.voicecalltranslator.feature.common.domain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.CallDataModelType
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.WebRtcRepository
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.ui.notification.CallNotificationManager
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import com.sergiolopez.voicecalltranslator.feature.common.domain.usecase.GetLanguageOptionUseCase
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class FirebaseService : Service() {

    @Inject
    lateinit var getConnectionUpdateUseCase: GetConnectionUpdateUseCase

    @Inject
    lateinit var firebaseAuthRepository: FirebaseAuthRepository

    @Inject
    lateinit var webRtcRepository: WebRtcRepository

    @Inject
    lateinit var getLanguageOptionUseCase: GetLanguageOptionUseCase

    private lateinit var callNotificationManager: CallNotificationManager

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    private var isConnectionUpdateRunning = false

    override fun onCreate() {
        Log.d("$VCT_LOGS FirebaseService: ", "onCreate")
        super.onCreate()
        callNotificationManager = CallNotificationManager(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                Log.d("$VCT_LOGS onStartCommand: ", startId.toString())
                startFirebaseService.invoke()
            }

            else -> {
                //throw IllegalArgumentException("Unknown action")
            }
        }

        return START_STICKY
    }

    private val startFirebaseService: () -> Unit = {
        scope.launch {
            firebaseAuthRepository.currentUser.collect { user ->
                user?.let {
                    startWebRtcManager(
                        user = it
                    )
                }
            }
        }
    }

    private suspend fun startWebRtcManager(user: User) {
        webRtcRepository.initFirebase(
            userId = user.id,
            startFirebaseService = startFirebaseService,
            scope = scope
        )
        webRtcRepository.initWebrtcClient(
            username = user.id,
            language = getLanguageOptionUseCase.invoke(user.id).name
        )
        if (!isConnectionUpdateRunning) {
            manageCall(user)
        }
    }

    private suspend fun manageCall(user: User) {
        isConnectionUpdateRunning = true
        val result = getConnectionUpdateUseCase.invoke(user.id)
        result.onSuccess {
            it.collect { call ->
                when (call.type) {
                    CallDataModelType.StartAudioCall -> {
                        val callData =
                            if (webRtcRepository.currentCall.value is Call.CallData) {
                                webRtcRepository.currentCall.value as Call.CallData
                            } else {
                                val newCallData = Call.CallData(
                                    callerId = call.sender ?: "",
                                    calleeId = call.target,
                                    isIncoming = true,
                                    callStatus = CallStatus.INCOMING_CALL,
                                    offerData = call.toString(),
                                    language = call.language,
                                    timestamp = call.timeStamp
                                )
                                webRtcRepository.setNewCallData(newCallData)
                                newCallData
                            }
                        launchIncomingCall(
                            callData = callData
                        )
                    }

                    CallDataModelType.EndCall -> {
                        val callData =
                            if (webRtcRepository.currentCall.value is Call.CallData) {
                                (webRtcRepository.currentCall.value as Call.CallData).copy(
                                    callStatus = CallStatus.CALL_FINISHED
                                )
                            } else {
                                val newCallData = Call.CallData(
                                    callerId = call.sender ?: "",
                                    calleeId = call.target,
                                    isIncoming = false,
                                    callStatus = CallStatus.CALL_FINISHED,
                                    offerData = call.toString(),
                                    language = call.language,
                                    timestamp = call.timeStamp
                                )
                                webRtcRepository.setNewCallData(newCallData)
                                newCallData
                            }
                        endCall(
                            callData = callData
                        )
                    }

                    else -> {
                        // DO NOTHING
                    }
                }
            }
        }
    }

    private fun launchIncomingCall(callData: Call.CallData) {
        Log.d("$VCT_LOGS launchIncomingCall: ", callData.toString())
        if (callData.callStatus == CallStatus.CALLING || callData.callStatus == CallStatus.CALL_IN_PROGRESS) {
            if (callData.isIncoming) {
                webRtcRepository.setTarget(callData.callerId)
                webRtcRepository.startCall(callData = callData)
            } else {
                // TODO : This is necessary?
                webRtcRepository.setTarget(callData.calleeId)
            }
        }
        callNotificationManager.updateCallNotification(callData)
    }

    private fun endCall(callData: Call.CallData) {
        Log.d("$VCT_LOGS endCall: ", callData.toString())
        callNotificationManager.updateCallNotification(callData)
        if (callData.isIncoming) {
            webRtcRepository.endCall(callData.callerId)
        } else {
            webRtcRepository.endCall(callData.calleeId)
        }
    }

    override fun onDestroy() {
        Log.d("$VCT_LOGS onDestroy: ", "scope.cancel()")
        scope.cancel()
        isConnectionUpdateRunning = false
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        internal const val ACTION_START_SERVICE = "start_service"
    }
}