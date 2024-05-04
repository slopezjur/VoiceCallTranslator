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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val webRTCClient: WebRTCClient,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val getConnectionUpdateUseCase: GetConnectionUpdateUseCase,
    private val clearCallUseCase: ClearCallUseCase
) {
    // Keeps track of the current TelecomCall state
    private var _currentCall: MutableStateFlow<Call> = MutableStateFlow(Call.CallNoData)
    val currentCall: StateFlow<Call>
        get() = _currentCall.asStateFlow()

    private lateinit var userId: String
    private lateinit var target: String

    private lateinit var scope: CoroutineScope

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

                        /*DataModelType.EndCall -> {
                            endCall(target)
                        }*/

                        else -> Unit
                    }
                }
            }
        }
    }

    fun sendConnectionRequest(
        sender: String,
        target: String,
        isVideoCall: Boolean
    ) {
        //scope = viewModelScope
        scope.launch {
            val dataModel = DataModel(
                sender = sender,
                type = if (isVideoCall) DataModelType.StartVideoCall else DataModelType.StartAudioCall,
                target = target
            )
            if (_currentCall.value is Call.CallData) {
                _currentCall.value = (_currentCall.value as Call.CallData).copy(
                    callerId = sender,
                    calleeId = target,
                    callStatus = CallStatus.CALLING
                )
            }
            sendConnectionUpdateUseCase.invoke(
                dataModel
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
                when (newState) {
                    PeerConnection.PeerConnectionState.NEW -> {
                        Unit
                    }

                    PeerConnection.PeerConnectionState.CONNECTING -> {
                        Unit
                    }

                    PeerConnection.PeerConnectionState.CONNECTED -> {
                        // 1. change my status to in call
                        //changeMyStatus(UserStatus.IN_CALL)
                        // 2. clear latest event inside my user section in firebase database
                        //onTransferEventToSocket()
                        updateCallStatus(
                            callStatus = CallStatus.CALL_IN_PROGRESS
                        )
                        Log.d("LET'S GOO!", "LET'S GOO!")
                    }

                    PeerConnection.PeerConnectionState.DISCONNECTED -> {
                        Unit
                    }

                    PeerConnection.PeerConnectionState.FAILED -> {
                        Unit
                    }

                    PeerConnection.PeerConnectionState.CLOSED -> {
                        Unit
                    }

                    null -> TODO()
                }
            }
        })
    }

    private fun updateCallStatus(callStatus: CallStatus) {
        if (_currentCall.value is Call.CallData) {
            _currentCall.value = (_currentCall.value as Call.CallData).copy(
                callStatus = callStatus
            )
        }
    }

    fun initLocalSurfaceView() {
        webRTCClient.initLocalSurfaceView()
    }

    fun setCallData(callData: Call.CallData) {
        _currentCall.value = callData
    }

    fun startCall() {
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
        webRTCClient.closeConnection()
        clearCall(userId = target)
        updateCallStatus(
            callStatus = CallStatus.CALL_FINISHED
        )
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
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

    fun endCurrentCall() {
        if (_currentCall.value is Call.CallData) {
            val callData = getCallData()
            sendEndCall(callData.callerId)
        }
    }

    fun isNewCall(): Boolean {
        return checkCallStatus(
            callStatus = CallStatus.CALLING
        )
    }

    fun isIncomingCall(): Boolean {
        return checkCallStatus(
            callStatus = CallStatus.INCOMING_CALL
        )
    }

    fun isInProgressCall(): Boolean {
        return checkCallStatus(
            callStatus = CallStatus.CALL_IN_PROGRESS
        )
    }

    private fun getCallData() = (_currentCall.value as Call.CallData)

    private fun checkCallStatus(callStatus: CallStatus): Boolean {
        return if (_currentCall.value is Call.CallData) {
            val callData = getCallData()
            callData.callStatus == callStatus
        } else {
            false
        }
    }
}