package com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge

import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.IceCandidateSerializer
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.MyPeerObserver
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.WebRtcClient
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.ClearCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetLastTranslationMessageUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.sql.Timestamp
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRtcRepository @Inject constructor(
    private val webRtcClient: WebRtcClient,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val getConnectionUpdateUseCase: GetConnectionUpdateUseCase,
    private val clearCallUseCase: ClearCallUseCase,
    private val getLastTranslationMessageUseCase: GetLastTranslationMessageUseCase
) {
    // Keeps track of the current Call state
    private var _currentCall: MutableStateFlow<Call> = MutableStateFlow(Call.CallNoData)
    val currentCall: StateFlow<Call>
        get() = _currentCall.asStateFlow()

    private lateinit var currentUserContact: Contact
    private lateinit var targetUserContact: Contact
    private lateinit var language: String
    private lateinit var startFirebaseService: () -> Unit

    private var scope: CoroutineScope? = null

    fun initWebrtcClient(username: String, language: String) {
        this.language = language
        subscribeToTranslationState()
        scope?.let {
            webRtcClient.setScope(scope = it)
            webRtcClient.initializeWebrtcClient(
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
                            webRtcClient.sendIceCandidate(targetUserContact, it)
                        }
                    }

                    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                        super.onConnectionChange(newState)
                        Log.d("$VCT_LOGS onConnectionChange", newState.toString())
                        when (newState) {
                            PeerConnection.PeerConnectionState.CONNECTED -> {
                                // 1. change my status to in call
                                //changeMyStatus(UserStatus.IN_CALL)
                                // 2. clear latest event inside my user section in firebase database
                                //onTransferEventToSocket()
                                updateCallStatus(
                                    callStatus = CallStatus.CALL_IN_PROGRESS
                                )
                                Log.d("$VCT_LOGS LET'S GOO!", "LET'S GOO!")
                            }

                            PeerConnection.PeerConnectionState.DISCONNECTED -> {
                                updateCallStatus(
                                    callStatus = CallStatus.RECONNECTING
                                )
                            }

                            else -> {
                                // DO NOTHING
                            }
                        }
                    }
                }
            )
        }
    }

    fun initFirebase(contact: Contact, scope: CoroutineScope, startFirebaseService: () -> Unit) {
        Log.d("$VCT_LOGS initFirebase: ", contact.toString())
        this.currentUserContact = contact
        this.startFirebaseService = startFirebaseService
        if (this.scope == null || !scope.isActive) {
            this.scope = scope
            this.scope?.launch {
                val result = getConnectionUpdateUseCase.invoke(contact.id)
                result.onSuccess {
                    it.collect { callDataModel ->
                        when (callDataModel.type) {
                            CallDataModelType.Offer -> {
                                webRtcClient.onRemoteSessionReceived(
                                    SessionDescription(
                                        SessionDescription.Type.OFFER,
                                        callDataModel.data.toString()
                                    )
                                )
                                try {
                                    webRtcClient.answer(
                                        targetUserContact = targetUserContact,
                                        targetLanguage = callDataModel.language
                                    )
                                    updateCallStatus(CallStatus.ANSWERING)
                                } catch (exception: Exception) {
                                    callDataModel.sender?.let { sender ->
                                        sendEndCall(
                                            targetContact = Contact(
                                                id = sender,
                                                email = callDataModel.senderEmail ?: ""
                                            )
                                        )
                                    }
                                }
                            }

                            CallDataModelType.Answer -> {
                                webRtcClient.onRemoteSessionReceived(
                                    SessionDescription(
                                        SessionDescription.Type.ANSWER,
                                        callDataModel.data.toString()
                                    )
                                )
                            }

                            CallDataModelType.IceCandidates -> {
                                val candidate: IceCandidate? = try {
                                    Json.decodeFromString(
                                        IceCandidateSerializer,
                                        callDataModel.data.toString()
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                                candidate?.let { iceCandidate ->
                                    webRtcClient.addIceCandidateToPeer(
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
    }

    fun setNewCallData(newCallData: Call.CallData) {
        _currentCall.value = newCallData
    }

    fun setTargetContact(targetUserContact: Contact) {
        this.targetUserContact = targetUserContact
    }

    fun sendConnectionRequest(
        targetContact: Contact,
    ) {
        this.targetUserContact = targetContact
        val callDataModel = CallDataModel(
            sender = this.currentUserContact.id,
            senderEmail = this.currentUserContact.email,
            target = targetUserContact.id,
            targetEmail = targetUserContact.email,
            type = CallDataModelType.StartAudioCall,
            language = language
        )

        _currentCall.value = Call.CallData(
            callerId = this.currentUserContact.id,
            callerEmail = this.currentUserContact.email,
            calleeId = targetUserContact.id,
            calleeEmail = targetUserContact.email,
            offerData = "",
            isIncoming = false,
            callStatus = CallStatus.CALLING,
            language = language,
            timestamp = Timestamp.from(Instant.now()).time
        )

        webRtcClient.startLocalStreaming()

        scope?.launch {
            sendConnectionUpdateUseCase.invoke(
                callDataModel
            )
        }
    }

    fun startCall(callData: Call.CallData) {
        webRtcClient.startLocalStreaming()
        _currentCall.value = callData.copy(
            callStatus = CallStatus.ANSWERING
        )
        webRtcClient.call(
            targetUserContact = targetUserContact,
            targetLanguage = callData.language
        )
    }

    fun sendEndCall(targetContact: Contact) {
        onTransferEventToSocket(
            CallDataModel(
                target = targetContact.id,
                targetEmail = targetContact.email,
                type = CallDataModelType.EndCall,
                language = language
            )
        )
        endCall()
    }

    fun endCall() {
        updateCallStatus(
            callStatus = CallStatus.CALL_FINISHED
        )
        webRtcClient.closeConnection()
        clearCall()
        _currentCall.value = Call.CallNoData
        startFirebaseService.invoke()
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRtcClient.toggleAudio(shouldBeMuted)
    }

    fun toggleSpeaker(shouldBeSpeaker: Boolean) {
        webRtcClient.toggleSpeaker(shouldBeSpeaker)
    }

    private fun onTransferEventToSocket(data: CallDataModel) {
        scope?.launch {
            sendConnectionUpdateUseCase.invoke(data)
        }
    }

    private fun clearCall() {
        scope?.launch {
            clearCallUseCase.invoke(currentUserContact.id)
        }
    }

    private fun updateCallStatus(callStatus: CallStatus) {
        if (_currentCall.value is Call.CallData) {
            _currentCall.value = (_currentCall.value as Call.CallData).copy(
                callStatus = callStatus
            )
        }
    }

    private fun subscribeToTranslationState() {
        scope?.launch {
            getLastTranslationMessageUseCase.invoke().collect { message ->
                onTransferEventToSocket(
                    CallDataModel(
                        sender = currentUserContact.id,
                        senderEmail = currentUserContact.email,
                        target = targetUserContact.id,
                        targetEmail = targetUserContact.email,
                        type = CallDataModelType.Message,
                        language = language,
                        message = message
                    )
                )
            }
        }
    }
}