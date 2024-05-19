package com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge

import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.ClearCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.IceCandidateSerializer
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.MyPeerObserver
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val webRTCClient: WebRTCClient,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val getConnectionUpdateUseCase: GetConnectionUpdateUseCase,
    private val clearCallUseCase: ClearCallUseCase
) {
    // Keeps track of the current Call state
    private var _currentCall: MutableStateFlow<Call> = MutableStateFlow(Call.CallNoData)
    val currentCall: StateFlow<Call>
        get() = _currentCall.asStateFlow()

    private lateinit var userId: String
    private lateinit var target: String
    private lateinit var startFirebaseService: () -> Unit

    private lateinit var scope: CoroutineScope

    fun initWebrtcClient(username: String, language: String) {
        webRTCClient.setScope(scope = scope)
        webRTCClient.initializeWebrtcClient(
            username = username,
            language = language,
            observer = object : MyPeerObserver() {

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    Log.d("$VCT_LOGS onAddStream", p0.toString())
                }

                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    Log.d("$VCT_LOGS onIceCandidate", p0.toString())
                    p0?.let {
                        webRTCClient.sendIceCandidate(target, it)
                    }
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    super.onConnectionChange(newState)
                    Log.d("$VCT_LOGS onConnectionChange", newState.toString())
                    if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                        // 1. change my status to in call
                        //changeMyStatus(UserStatus.IN_CALL)
                        // 2. clear latest event inside my user section in firebase database
                        //onTransferEventToSocket()
                        updateCallStatus(
                            callStatus = CallStatus.CALL_IN_PROGRESS
                        )
                        Log.d("$VCT_LOGS LET'S GOO!", "LET'S GOO!")
                    }
                }
            }
        )
    }

    fun initFirebase(userId: String, scope: CoroutineScope, startFirebaseService: () -> Unit) {
        Log.d("$VCT_LOGS initFirebase: ", userId)
        this.userId = userId
        this.startFirebaseService = startFirebaseService
        this.scope = scope
        this.scope.launch {
            val result = getConnectionUpdateUseCase.invoke(userId)
            result.onSuccess {
                it.collect { event ->
                    when (event.type) {
                        DataModelType.Offer -> {
                            webRTCClient.onRemoteSessionReceived(
                                SessionDescription(
                                    SessionDescription.Type.OFFER,
                                    event.data.toString()
                                )
                            )
                            try {
                                webRTCClient.answer(target)
                                updateCallStatus(CallStatus.ANSWERING)
                            } catch (exception: Exception) {
                                event.sender?.let { sender ->
                                    sendEndCall(sender)
                                }
                            }
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
                                Json.decodeFromString(IceCandidateSerializer, event.data.toString())
                            } catch (e: Exception) {
                                null
                            }
                            candidate?.let { iceCandidate ->
                                webRTCClient.addIceCandidateToPeer(
                                    iceCandidate = iceCandidate
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun setNewCallData(newCallData: Call.CallData) {
        _currentCall.value = newCallData
    }

    fun setTarget(target: String) {
        this.target = target
    }

    fun sendConnectionRequest(
        target: String,
    ) {
        this.target = target
        val dataModel = DataModel(
            sender = userId,
            type = DataModelType.StartAudioCall,
            target = target
        )

        _currentCall.value = Call.CallData(
            callerId = userId,
            calleeId = target,
            offerData = "",
            answerData = "",
            isIncoming = false,
            callStatus = CallStatus.CALLING,
            timestamp = Instant.now().epochSecond
        )

        webRTCClient.startLocalStreaming()

        scope.launch {
            sendConnectionUpdateUseCase.invoke(
                dataModel
            )
        }
    }

    fun startCall(callData: Call.CallData) {
        webRTCClient.startLocalStreaming()
        _currentCall.value = callData.copy(
            callStatus = CallStatus.ANSWERING
        )
        webRTCClient.call(target)
    }

    /*private fun endCall(target: String) {
        webRTCClient.closeConnection()
        clearCall(userId = target)
    }*/

    fun sendEndCall(target: String) {
        onTransferEventToSocket(
            DataModel(
                type = DataModelType.EndCall,
                target = target
            )
        )
        endCall(target = target)
    }

    fun endCall(target: String) {

        updateCallStatus(
            callStatus = CallStatus.CALL_FINISHED
        )
        webRTCClient.closeConnection()
        clearCall(target = target)
        _currentCall.value = Call.CallNoData
        startFirebaseService.invoke()
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
    }

    private fun onTransferEventToSocket(data: DataModel) {
        scope.launch {
            sendConnectionUpdateUseCase.invoke(data)
        }
    }

    private fun clearCall(target: String) {
        scope.launch {
            clearCallUseCase.invoke(target)
        }
    }

    private fun updateCallStatus(callStatus: CallStatus) {
        if (_currentCall.value is Call.CallData) {
            _currentCall.value = (_currentCall.value as Call.CallData).copy(
                callStatus = callStatus
            )
        }
    }

    private fun getCallData() = (_currentCall.value as Call.CallData)
}