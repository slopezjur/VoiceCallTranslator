package com.sergiolopez.voicecalltranslator.feature.call.telecom.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.telecom.notification.CallNotificationAction
import com.sergiolopez.voicecalltranslator.feature.call.telecom.notification.CallNotificationManager
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class CallBroadcast @Inject constructor() : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(context: Context, intent: Intent) {
        val callData = intent.getCallData()
        val action = intent.getNotificationCallAction()

        when {
            callData == null || action == null -> {
                // If for some reason notification is "corrupted", we dispatched!
                if (mainRepository.currentCall.value is Call.CallData) {
                    CallNotificationManager(context).updateCallNotification(mainRepository.currentCall.value as Call.CallData)
                } else {
                    dispatchNotification(
                        context = context
                    )
                }
            }

            else -> {
                when (action) {
                    CallNotificationAction.Answer -> {
                        mainRepository.setTarget(target = callData.callerId)
                        mainRepository.startCall(callData = callData)
                        CallNotificationManager(context).updateCallNotification(callData)
                    }

                    CallNotificationAction.Disconnect -> {
                        mainRepository.setTarget(target = callData.callerId)
                        mainRepository.sendEndCall(target = callData.callerId)
                        CallNotificationManager(context).updateCallNotification(callData)
                    }
                }
            }
        }
    }

    private fun Intent.getCallData(): Call.CallData? {
        val callData = getStringExtra(
            CallNotificationManager.CALL_DATA_ACTION
        )
        return callData?.let {
            Json.decodeFromString(
                Call.CallData.serializer(),
                callData
            )
        }
    }

    private fun Intent.getNotificationCallAction() =
        // TODO : Simple data, remove Parcelable?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(
                CallNotificationManager.CALL_NOTIFICATION_ACTION,
                CallNotificationAction::class.java,
            )
        } else {
            getParcelableExtra(CallNotificationManager.CALL_NOTIFICATION_ACTION)
        }

    private fun dispatchNotification(context: Context) {
        CallNotificationManager(context).updateCallNotification(
            Call.CallData(
                callerId = "",
                calleeId = "",
                offerData = "",
                answerData = "",
                isIncoming = false,
                callStatus = CallStatus.CALL_FINISHED,
                timestamp = 0
            )
        )
    }
}
