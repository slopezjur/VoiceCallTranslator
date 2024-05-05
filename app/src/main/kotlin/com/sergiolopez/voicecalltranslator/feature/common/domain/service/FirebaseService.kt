package com.sergiolopez.voicecalltranslator.feature.common.domain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.telecom.notification.CallNotificationManager
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModelType
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.MainRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : Service() {

    @Inject
    lateinit var getConnectionUpdateUseCase: GetConnectionUpdateUseCase

    @Inject
    lateinit var firebaseAuthService: FirebaseAuthService

    @Inject
    lateinit var mainRepository: MainRepository

    private lateinit var callNotificationManager: CallNotificationManager

    companion object {
        internal const val ACTION_START_SERVICE = "start_service"
    }

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        callNotificationManager = CallNotificationManager(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            firebaseAuthService.currentUser.collect { user ->
                user?.let {
                    when (intent?.action) {
                        ACTION_START_SERVICE -> startService(it)

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
        mainRepository.initWebrtcClient(username = user.id)
        manageCall(user)
    }

    private fun manageCall(user: User) {
        scope.launch {
            val result = getConnectionUpdateUseCase.invoke(user.id)
            if (result.isSuccess) {
                result.getOrThrow().collect { call ->
                    when (call.type) {
                        DataModelType.StartAudioCall -> {
                            val callData = if (mainRepository.currentCall.value is Call.CallData) {
                                mainRepository.currentCall.value as Call.CallData
                            } else {
                                Call.CallData(
                                    callerId = call.sender ?: "",
                                    calleeId = call.target,
                                    isIncoming = true,
                                    callStatus = CallStatus.INCOMING_CALL,
                                    offerData = call.toString(),
                                    answerData = "",
                                    timestamp = call.timeStamp
                                )
                            }
                            launchIncomingCall(
                                callData = callData
                            )
                        }

                        DataModelType.EndCall -> {
                            //initWebrtcClient(user)
                            val callData = if (mainRepository.currentCall.value is Call.CallData) {
                                mainRepository.currentCall.value as Call.CallData
                            } else {
                                Call.CallData(
                                    callerId = call.sender ?: "",
                                    calleeId = call.target,
                                    isIncoming = false,
                                    callStatus = CallStatus.CALL_FINISHED,
                                    offerData = call.toString(),
                                    answerData = "",
                                    timestamp = call.timeStamp
                                )
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
    }

    private fun launchIncomingCall(callData: Call.CallData) {
        callNotificationManager.updateCallNotification(callData)
        if (callData.callStatus == CallStatus.CALLING || callData.callStatus == CallStatus.CALL_IN_PROGRESS) {
            if (callData.isIncoming) {
                mainRepository.setTarget(callData.callerId)
                mainRepository.startCall(callData = callData)
            } else {
                // TODO : This is necessary?
                mainRepository.setTarget(callData.calleeId)
            }
        }
    }

    private fun endCall(callData: Call.CallData) {
        callNotificationManager.updateCallNotification(callData)
        /*if (callData.isIncoming) {
            mainRepository.sendEndCall(callData.callerId)
        } else {

            mainRepository.sendEndCall(callData.calleeId)
        }*/
    }

    private fun isValidMainRepoCallData(): Boolean {
        return if (mainRepository.currentCall.value is Call.CallData) {
            getCallData().callStatus != CallStatus.CALL_IN_PROGRESS
        } else {
            true
        }
    }

    private fun getCallData(): Call.CallData {
        return mainRepository.currentCall.value as Call.CallData
    }

    override fun onBind(p0: Intent?): IBinder? = null

}