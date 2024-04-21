package com.sergiolopez.voicecalltranslator.feature.call.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService
import javax.inject.Singleton

@Singleton
class TelecomCallManager {

    companion object {
        fun Context.launchCall(action: String, name: String, uri: Uri) {
            startService(
                Intent(this, TelecomCallService::class.java).apply {
                    this.action = action
                    putExtra(TelecomCallService.EXTRA_NAME, name)
                    putExtra(TelecomCallService.EXTRA_URI, uri)
                },
            )
        }
    }
}