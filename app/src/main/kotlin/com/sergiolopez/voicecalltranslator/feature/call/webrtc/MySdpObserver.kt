package com.sergiolopez.voicecalltranslator.feature.call.webrtc

import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class MySdpObserver : SdpObserver {
    override fun onCreateSuccess(desc: SessionDescription?) {
        Log.d("$VCT_LOGS onCreateSuccess: ", "onCreateSuccess " + desc.toString())
    }

    override fun onSetSuccess() {
        Log.d("$VCT_LOGS onSetSuccess: ", "onSetSuccess ")
    }

    override fun onCreateFailure(p0: String?) {
        Log.d("$VCT_LOGS onCreateFailure: ", "onCreateFailure " + p0.toString())
    }

    override fun onSetFailure(p0: String?) {
        Log.d("$VCT_LOGS onCreateSuccess: ", "onCreateSuccess " + p0.toString())
    }
}