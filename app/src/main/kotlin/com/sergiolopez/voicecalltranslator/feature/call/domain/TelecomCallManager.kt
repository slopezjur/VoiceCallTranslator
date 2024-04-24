package com.sergiolopez.voicecalltranslator.feature.call.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelecomCallManager @Inject constructor() {

    companion object {

        fun Context.startNewCall(name: String, uri: Uri) {
            startService(
                Intent(this, TelecomCallService::class.java).apply {
                    this.action = TelecomCallService.ACTION_OUTGOING_CALL
                    putExtra(TelecomCallService.EXTRA_NAME, name)
                    putExtra(TelecomCallService.EXTRA_URI, uri)
                },
            )
        }

        fun Context.launchIncomingCall(name: String, uri: Uri) {
            startService(
                Intent(this, TelecomCallService::class.java).apply {
                    this.action = TelecomCallService.ACTION_INCOMING_CALL
                    putExtra(TelecomCallService.EXTRA_NAME, name)
                    putExtra(TelecomCallService.EXTRA_URI, uri)
                },
            )
        }
    }
}