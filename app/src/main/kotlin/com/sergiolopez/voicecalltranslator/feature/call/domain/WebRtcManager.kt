package com.sergiolopez.voicecalltranslator.feature.call.domain

import android.net.Uri
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.AnswerCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.IceCandidateSerializer
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.MyPeerObserver
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModel
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModelType
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class WebRtcManager @Inject constructor(
    private val webRTCClient: WebRTCClient,
    private val firebaseAuthService: FirebaseAuthService,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val answerCallUseCase: AnswerCallUseCase
) {

    private var target: String? = null
    private var user: User.UserData? = null

    init {
        GlobalScope.launch {
            firebaseAuthService.currentUser.collect {
                if (it is User.UserData) {
                    user = it
                }
            }
        }
    }

    fun managerWebRtc(dataModelType: DataModelType, address: Uri) {
        val callData = Json.decodeFromString<Call.CallData>(address.toString())
        when (dataModelType) {
            DataModelType.Offer -> {
                webRTCClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.OFFER,
                        callData.offerData
                    )
                )
                webRTCClient.answer(callData.callerId)
            }

            DataModelType.Answer -> {
                webRTCClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.ANSWER,
                        callData.answerData
                    )
                )
            }

            DataModelType.IceCandidates -> {
                val candidate: IceCandidate? = try {
                    Json.decodeFromString(IceCandidateSerializer, callData.offerData.toString())
                } catch (e: Exception) {
                    null
                }
                candidate?.let {
                    webRTCClient.addIceCandidateToPeer(it)
                }
            }

            DataModelType.EndCall -> {
                //listener?.endCall()
                endCall()
                initWebrtcClient(callData.callerId)
            }

            else -> Unit
        }
    }

    fun sendConnectionRequest(target: String) {
        /*onTransferEventToSocket(
            DataModel(
                type = DataModelType.StartAudioCall,
                target = target
            )
        )*/
    }

    fun setTarget(target: String) {
        this.target = target
    }

    fun initWebrtcClient(username: String) {
        webRTCClient.initializeWebrtcClient(username, object : MyPeerObserver() {

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                try {
                    p0?.videoTracks?.get(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRTCClient.sendIceCandidate(target!!, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                super.onConnectionChange(newState)
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    // 1. change my status to in call
                    //changeMyStatus(UserStatus.IN_CALL)
                    // 2. clear latest event inside my user section in firebase database
                    //firebaseClient.clearLatestEvent()
                }
            }
        })
    }

    fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        webRTCClient.initLocalSurfaceView(view, isVideoCall)
    }

    fun startCall() {
        webRTCClient.call(target!!)
    }

    fun endCall() {
        webRTCClient.closeConnection()
        //changeMyStatus(UserStatus.ONLINE)
    }

    fun sendEndCall() {
        /*onTransferEventToSocket(
            DataModel(
                type = EndCall,
                target = target!!
            )
        )*/
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
    }

    fun onTransferEventToSocket(data: DataModel) {
        TODO("Not yet implemented")
    }

    /*@OptIn(DelicateCoroutinesApi::class)
    override fun onTransferEventToSocketCall(data: DataModel) {
        // TODO : GlobalFail?
        GlobalScope.launch {
            createCallUseCase.invoke(
                Call.CallData(
                    callerId = user?.id ?: "ERROR WTF",
                    calleeId = data.target,
                    offerData = data.data,
                    answerData = "",
                    isIncoming = false,
                    callStatus = CallStatus.CALLING,
                    timestamp = Instant.now().epochSecond
                )
            )

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onTransferEventToSocketAnswer(data: DataModel) {
        // TODO : GlobalFail?
        GlobalScope.launch {
            createCallUseCase.invoke(
                Call.CallData(
                    callerId = user?.id ?: "ERROR WTF",
                    calleeId = data.target,
                    offerData = "",
                    answerData = data.data,
                    isIncoming = false,
                    callStatus = CallStatus.CALL_IN_PROGRESS,
                    timestamp = Instant.now().epochSecond
                )
            )

        }
    }

    override fun answer(displayName: CharSequence, address: Uri) {
        webRTCClient.answer(
            target = target ?: displayName.toString()
        )
    }*/
}