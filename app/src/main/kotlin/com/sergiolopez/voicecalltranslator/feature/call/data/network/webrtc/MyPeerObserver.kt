package com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc

import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver

open class MyPeerObserver : PeerConnection.Observer {

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Log.d("${VctGlobalName.VCT_LOGS} onSignalingChange: ", "onSignalingChange " + p0.toString())
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Log.d(
            "${VctGlobalName.VCT_LOGS} onIceConnectionChange: ",
            "onIceConnectionChange " + p0.toString()
        )
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Log.d(
            "${VctGlobalName.VCT_LOGS} onIceConnectionReceivingChange: ",
            "onIceConnectionReceivingChange $p0"
        )
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Log.d(
            "${VctGlobalName.VCT_LOGS} onIceGatheringChange: ",
            "onIceGatheringChange " + p0.toString()
        )
    }

    override fun onIceCandidate(p0: IceCandidate?) {
        Log.d("${VctGlobalName.VCT_LOGS} onIceCandidate: ", "onIceCandidate " + p0.toString())
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Log.d(
            "${VctGlobalName.VCT_LOGS} onIceCandidatesRemoved: ",
            "onIceCandidatesRemoved " + p0.toString()
        )
    }

    override fun onAddStream(p0: MediaStream?) {
        Log.d("${VctGlobalName.VCT_LOGS} onAddStream: ", "onAddStream " + p0.toString())
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Log.d("${VctGlobalName.VCT_LOGS} onRemoveStream: ", "onRemoveStream " + p0.toString())
    }

    override fun onDataChannel(p0: DataChannel?) {
        Log.d("${VctGlobalName.VCT_LOGS} onDataChannel: ", "onDataChannel " + p0.toString())
    }

    override fun onRenegotiationNeeded() {
        Log.d("${VctGlobalName.VCT_LOGS} onRenegotiationNeeded: ", "onRenegotiationNeeded")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Log.d("${VctGlobalName.VCT_LOGS} onAddTrack: ", "onAddTrack " + p0.toString())
    }
}