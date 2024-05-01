package com.sergiolopez.voicecalltranslator.feature.call.domain

import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.AnswerCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.MyPeerObserver
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModel
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRtcManagerB @Inject constructor(
    private val webRTCClient: WebRTCClient,
    private val firebaseAuthService: FirebaseAuthService,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val answerCallUseCase: AnswerCallUseCase
) {

    private var target: String? = null

    init {
        setUpWebRtc(webRTCClient = webRTCClient)
    }

    fun setTarget(target: String) {
        this.target = target
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setUpWebRtc(webRTCClient: WebRTCClient) {
        // TODO : GlobalFail?
        GlobalScope.launch {
            firebaseAuthService.currentUser.collect { user ->
                if (user is User.UserData) {
                    webRTCClient.initializeWebrtcClient(user.id, object : MyPeerObserver() {

                        override fun onAddStream(p0: MediaStream?) {
                            super.onAddStream(p0)
                            try {
                                //p0?.audioTracks?.get(0)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun onIceCandidate(p0: IceCandidate?) {
                            super.onIceCandidate(p0)
                            p0?.let {
                                // TODO : Remove exclamations
                                webRTCClient.sendIceCandidate(target!!, it)
                            }
                        }

                        override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                            super.onConnectionChange(newState)
                            if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                                TODO("Not yet implemented")
                                // 1. change my status to in call
                                //firebaseClient.changeMyStatus(UserStatus.IN_CALL)
                                // 2. clear latest event inside my user section in firebase database
                                //firebaseClient.clearLatestEvent()
                            }
                        }
                    })
                }
            }
        }
    }

    fun onTransferEventToSocket(data: DataModel) {
        TODO("Not yet implemented")
    }

    /*@OptIn(DelicateCoroutinesApi::class)
    override fun onTransferEventToSocketCall(data: DataModel) {
        // TODO : GlobalFail?
        GlobalScope.launch {
            firebaseAuthService.currentUser.collect { user ->
                if (user is User.UserData) {
                    createCallUseCase.invoke(
                        Call.CallData(
                            callerId = user.id,
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
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onTransferEventToSocketAnswer(data: DataModel) {
        // TODO : GlobalFail?
        GlobalScope.launch {
            firebaseAuthService.currentUser.collect { user ->
                if (user is User.UserData) {
                    answerCallUseCase.invoke(
                        Call.CallData(
                            callerId = user.id,
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
        }
    }

    override fun answer(displayName: CharSequence, address: Uri) {
        webRTCClient.answer(
            target = target ?: displayName.toString()
        )
    }*/
}