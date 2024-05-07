/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sergiolopez.voicecalltranslator.feature.call.telecom.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.PermissionChecker
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorActivity
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.telecom.broadcast.CallBroadcast
import kotlinx.serialization.json.Json

class CallNotificationManager(private val context: Context) {

    internal companion object {
        const val CALL_NOTIFICATION_ID = 200
        const val CALL_DATA_ACTION = "call_data_action"
        const val CALL_NOTIFICATION_ACTION = "call_notification_action"
        const val CALL_NOTIFICATION_INCOMING_CHANNEL_ID = "call_incoming_channel"
        const val CALL_NOTIFICATION_ONGOING_CHANNEL_ID = "call_ongoing_channel"

        private val ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    }

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    fun updateCallNotification(call: Call.CallData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            return
        }

        createNotificationChannels()

        when (call.callStatus) {
            CallStatus.CALL_FINISHED -> {
                notificationManager.cancel(CALL_NOTIFICATION_ID)
            }

            CallStatus.INCOMING_CALL,
            CallStatus.CALL_IN_PROGRESS -> {
                val notification = createNotification(call)
                notificationManager.notify(CALL_NOTIFICATION_ID, notification)
            }

            else -> {
                Unit
            }
        }
    }

    private fun createNotification(callData: Call.CallData): Notification {
        val caller = Person.Builder()
            .setName(callData.callerId)
            .setUri(Uri.parse("Calling").toString())
            .setImportant(true)
            .build()

        val contentIntent = PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */
            Intent(context, VoiceCallTranslatorActivity::class.java).apply {
                putExtra(
                    VoiceCallTranslatorActivity.CALL_DATA_FROM_NOTIFICATION,
                    Json.encodeToString(Call.CallData.serializer(), callData)
                )
            },
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val isIncoming = callData.isIncoming && (callData.callStatus == CallStatus.INCOMING_CALL)
        val callStyle = if (isIncoming) {
            NotificationCompat.CallStyle.forIncomingCall(
                caller,
                getPendingIntent(
                    callData = callData.copy(
                        callStatus = CallStatus.CALL_FINISHED
                    ),
                    callNotificationAction = CallNotificationAction.Disconnect
                ),
                getPendingIntent(
                    callData = callData.copy(
                        callStatus = CallStatus.CALL_IN_PROGRESS
                    ),
                    callNotificationAction = CallNotificationAction.Answer
                ),
            )
        } else {
            NotificationCompat.CallStyle.forOngoingCall(
                caller,
                getPendingIntent(
                    callData = callData.copy(
                        callStatus = CallStatus.CALL_FINISHED
                    ),
                    callNotificationAction = CallNotificationAction.Disconnect
                ),
            )
        }
        val channelId = if (isIncoming) {
            CALL_NOTIFICATION_INCOMING_CHANNEL_ID
        } else {
            CALL_NOTIFICATION_ONGOING_CHANNEL_ID
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentIntent(contentIntent)
            .setFullScreenIntent(contentIntent, true)
            .setSmallIcon(R.drawable.ic_round_call_24)
            .setOngoing(true)
            .setStyle(callStyle)

        // TODO figure out why custom actions are not working
        /*if (call.isOnHold) {
            builder.addAction(
                R.drawable.ic_phone_paused_24, "Resume",
                getPendingIntent(
                    TelecomCallAction.Activate,
                ),
            )
        }*/
        return builder.build()
    }

    private fun getPendingIntent(
        callData: Call.CallData,
        callNotificationAction: CallNotificationAction
    ): PendingIntent {
        val callIntent = Intent(context, CallBroadcast::class.java).apply {
            putExtra(
                CALL_DATA_ACTION,
                Json.encodeToString(Call.CallData.serializer(), callData)
            )
            putExtra(
                CALL_NOTIFICATION_ACTION,
                callNotificationAction,
            )
        }

        return PendingIntent.getBroadcast(
            context,
            callIntent.hashCode(),
            callIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannels() {
        val incomingChannel = NotificationChannelCompat.Builder(
            CALL_NOTIFICATION_INCOMING_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH,
        ).setName("Incoming calls")
            .setDescription("Handles the notifications when receiving a call")
            .setVibrationEnabled(true).setSound(
                ringToneUri,
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build(),
            ).build()

        val ongoingChannel = NotificationChannelCompat.Builder(
            CALL_NOTIFICATION_ONGOING_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
        ).setName("Ongoing calls").setDescription("Displays the ongoing call notifications").build()

        notificationManager.createNotificationChannelsCompat(
            listOf(
                incomingChannel,
                ongoingChannel,
            ),
        )
    }
}
