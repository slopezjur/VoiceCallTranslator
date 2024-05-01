package com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge

import android.content.Intent
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.ClearCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.IceCandidateSerializer
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.MyPeerObserver
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val webRTCClient: WebRTCClient,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val getConnectionUpdateUseCase: GetConnectionUpdateUseCase,
    private val clearCallUseCase: ClearCallUseCase
) {
    private lateinit var target: String
    private var remoteView: SurfaceViewRenderer? = null

    private lateinit var scope: CoroutineScope

    fun login(username: String, password: String, isDone: (Boolean, String?) -> Unit) {
        //firebaseClient.login(username, password, isDone)
    }

    fun observeUsersStatus(status: (List<Pair<String, String>>) -> Unit) {
        //firebaseClient.observeUsersStatus(status)
    }

    fun initFirebase(userId: String, scope: CoroutineScope) {
        this.target = userId
        this.scope = scope
        this.scope.launch {
            val result = getConnectionUpdateUseCase.invoke(userId)
            if (result.isSuccess) {
                result.getOrThrow().collect { event ->
                    //target = event.target
                    when (event.type) {
                        DataModelType.Offer -> {
                            webRTCClient.onRemoteSessionReceived(
                                SessionDescription(
                                    SessionDescription.Type.OFFER,
                                    event.data.toString()
                                )
                            )
                            webRTCClient.answer(target)
                        }

                        DataModelType.Answer -> {
                            webRTCClient.onRemoteSessionReceived(
                                SessionDescription(
                                    SessionDescription.Type.ANSWER,
                                    event.data.toString()
                                )
                            )
                        }

                        DataModelType.IceCandidates -> {
                            val candidate: IceCandidate? = try {
                                //gson.fromJson(event.data.toString(),IceCandidate::class.java)
                                Json.decodeFromString(IceCandidateSerializer, event.data.toString())
                            } catch (e: Exception) {
                                null
                            }
                            candidate?.let {
                                webRTCClient.addIceCandidateToPeer(it)
                            }
                        }

                        DataModelType.EndCall -> {
                            endCall(target)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun sendConnectionRequest(
        sender: String,
        target: String,
        isVideoCall: Boolean,
        success: (Boolean) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        //scope = viewModelScope
        scope.launch {
            sendConnectionUpdateUseCase.invoke(
                DataModel(
                    sender = sender,
                    type = if (isVideoCall) DataModelType.StartVideoCall else DataModelType.StartAudioCall,
                    target = target
                )
            )
        }
    }

    fun setTarget(target: String) {
        this.target = target
    }

    fun initWebrtcClient(username: String) {
        webRTCClient.setScope(scope = scope)
        webRTCClient.initializeWebrtcClient(username, object : MyPeerObserver() {

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                Log.d("VCT_LOGS onAddStream", p0.toString())
                try {
                    p0?.videoTracks?.get(0)?.addSink(remoteView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                Log.d("VCT_LOGS onIceCandidate", p0.toString())
                p0?.let {
                    webRTCClient.sendIceCandidate(target, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                super.onConnectionChange(newState)
                Log.d("VCT_LOGS onConnectionChange", newState.toString())
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    // 1. change my status to in call
                    //changeMyStatus(UserStatus.IN_CALL)
                    // 2. clear latest event inside my user section in firebase database
                    //onTransferEventToSocket()
                    Log.d("LET'S GOO!", "LET'S GOO!")
                }
            }
        })
    }

    fun initLocalSurfaceView() {
        webRTCClient.initLocalSurfaceView()
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        webRTCClient.initRemoteSurfaceView(view)
        this.remoteView = view
    }

    fun startCall() {
        webRTCClient.call(target)
    }

    private fun endCall(target: String) {
        webRTCClient.closeConnection()
        clearCall(userId = target)
        //changeMyStatus(UserStatus.ONLINE)
    }

    fun sendEndCall(target: String) {
        onTransferEventToSocket(
            DataModel(
                type = DataModelType.EndCall,
                target = target
            )
        )
        endCall(target)
    }

    /*private fun changeMyStatus(status: UserStatus) {
        firebaseClient.changeMyStatus(status)
    }*/

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        webRTCClient.toggleVideo(shouldBeMuted)
    }

    fun switchCamera() {
        webRTCClient.switchCamera()
    }

    private fun onTransferEventToSocket(data: DataModel) {
        scope.launch {
            sendConnectionUpdateUseCase.invoke(data)
        }
    }

    private fun clearCall(userId: String) {
        scope.launch {
            clearCallUseCase.invoke(userId)
        }
    }

    fun setScreenCaptureIntent(screenPermissionIntent: Intent) {
        webRTCClient.setPermissionIntent(screenPermissionIntent)
    }

    fun toggleScreenShare(isStarting: Boolean) {
        if (isStarting) {
            webRTCClient.startScreenCapturing()
        } else {
            webRTCClient.stopScreenCapturing()
        }
    }
}