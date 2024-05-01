package com.sergiolopez.voicecalltranslator.feature.call.webrtc

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class MySdpObserver : SdpObserver {
    override fun onCreateSuccess(desc: SessionDescription?) {
        Log.d("MySdpObserverSessionDescription: ", "onCreateSuccess " + desc.toString())
    }

    override fun onSetSuccess() {
        Log.d("MySdpObserverSessionDescription: ", "onSetSuccess ")
    }

    override fun onCreateFailure(p0: String?) {
        Log.d("MySdpObserverSessionDescription: ", "onCreateFailure " + p0.toString())
    }

    override fun onSetFailure(p0: String?) {
        Log.d("MySdpObserverSessionDescription: ", "onCreateSuccess " + p0.toString())
    }
}