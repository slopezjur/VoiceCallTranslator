package com.sergiolopez.voicecalltranslator.feature.call.ui.notification.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorActivity
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.WebRtcRepository
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.ui.notification.CallNotificationAction
import com.sergiolopez.voicecalltranslator.feature.call.ui.notification.CallNotificationManager
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class CallBroadcast @Inject constructor() : BroadcastReceiver() {

    @Inject
    lateinit var webRtcRepository: WebRtcRepository

    override fun onReceive(context: Context, intent: Intent) {
        val callData = intent.getCallData()
        val action = intent.getNotificationCallAction()

        when {
            callData == null || action == null -> {
                // If for some reason notification is "corrupted", we dispatched!
                if (webRtcRepository.currentCall.value is Call.CallData) {
                    CallNotificationManager(context).updateCallNotification(webRtcRepository.currentCall.value as Call.CallData)
                } else {
                    dispatchNotification(
                        context = context
                    )
                }
            }

            else -> {
                val targetContact = if (callData.isIncoming) {
                    Contact(
                        id = callData.callerId,
                        email = callData.callerEmail
                    )
                } else {
                    Contact(
                        id = callData.calleeId,
                        email = callData.calleeEmail
                    )
                }

                when (action) {
                    CallNotificationAction.Answer -> {
                        webRtcRepository.setTargetContact(targetUserContact = targetContact)
                        webRtcRepository.startCall(callData = callData)
                        CallNotificationManager(context).updateCallNotification(callData)
                        startVoiceCallTranslator(context, callData)
                    }

                    CallNotificationAction.Disconnect -> {
                        webRtcRepository.setTargetContact(targetUserContact = targetContact)
                        webRtcRepository.sendEndCall(
                            targetContact = targetContact
                        )
                        CallNotificationManager(context).updateCallNotification(
                            call = callData
                        )
                    }
                }
            }
        }
    }

    private fun startVoiceCallTranslator(
        context: Context,
        callData: Call.CallData
    ) {
        context.startActivity(
            Intent(context, VoiceCallTranslatorActivity::class.java).apply {
                putExtra(
                    VoiceCallTranslatorActivity.CALL_DATA_FROM_NOTIFICATION,
                    Json.encodeToString(Call.CallData.serializer(), callData)
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
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
                callerEmail = "",
                calleeId = "",
                calleeEmail = "",
                offerData = "",
                isIncoming = false,
                callStatus = CallStatus.CALL_FINISHED,
                language = "",
                timestamp = 0
            )
        )
    }
}