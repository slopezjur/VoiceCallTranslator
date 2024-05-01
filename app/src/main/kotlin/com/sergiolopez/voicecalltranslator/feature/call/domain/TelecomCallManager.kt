package com.sergiolopez.voicecalltranslator.feature.call.domain

import android.content.Context
import android.content.Intent
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelecomCallManager @Inject constructor() {

    companion object {

        fun Context.startNewCall(call: Call.CallData) {
            startService(
                Intent(this, TelecomCallService::class.java).apply {
                    this.action = TelecomCallService.ACTION_OUTGOING_CALL
                    putExtra(TelecomCallService.EXTRA_CALL, call)
                },
            )
        }

        fun Context.launchIncomingCall(call: Call.CallData) {
            startService(
                Intent(this, TelecomCallService::class.java).apply {
                    this.action = TelecomCallService.ACTION_INCOMING_CALL
                    putExtra(TelecomCallService.EXTRA_CALL, call)
                },
            )
        }
    }
}