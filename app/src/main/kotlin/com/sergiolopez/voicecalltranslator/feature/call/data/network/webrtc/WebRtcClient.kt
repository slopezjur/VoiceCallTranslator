package com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc

import android.content.Context
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.CallDataModel
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.CallDataModelType
import com.sergiolopez.voicecalltranslator.feature.call.domain.audio.MagicAudioProcessor
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.OPEN_AI_SAMPLE_RATE
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.webrtc.AudioTrack
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpSender
import org.webrtc.SessionDescription
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.voiceengine.WebRtcAudioUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRtcClient @Inject constructor(
    private val context: Context,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase,
    private val magicAudioProcessor: MagicAudioProcessor
) {
    private lateinit var username: String
    private lateinit var language: String

    //webrtc variables
    private val peerConnectionFactory by lazy {
        createPeerConnectionFactory()
    }
    private var peerConnection: PeerConnection? = null
    private val iceServer = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer(),
        PeerConnection.IceServer.builder("stun:stun.relay.metered.ca:80")
            .createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80")
            .setUsername("cb206d600f88b1849b99f06c")
            .setPassword("UflvJt/7UzWqVPF1").createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80?transport=tcp")
            .setUsername("cb206d600f88b1849b99f06c")
            .setPassword("UflvJt/7UzWqVPF1").createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:443")
            .setUsername("cb206d600f88b1849b99f06c")
            .setPassword("UflvJt/7UzWqVPF1").createIceServer(),
        PeerConnection.IceServer.builder("turns:global.relay.metered.ca:443?transport=tcp")
            .setUsername("cb206d600f88b1849b99f06c")
            .setPassword("UflvJt/7UzWqVPF1").createIceServer()
    )
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }
    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))

        WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler()
    }

    //call variables
    private var localTrackId = ""
    private var localAudioTrack: AudioTrack? = null
    private var rtpSenderTrack: RtpSender? = null

    private lateinit var scope: CoroutineScope

    init {
        initPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        // TODO : Use WebRTC improvements for audio quality?
        val adm = JavaAudioDeviceModule.builder(context)
            .setAudioRecordDataCallback(magicAudioProcessor)
            .setSampleRate(OPEN_AI_SAMPLE_RATE)
            .setUseHardwareNoiseSuppressor(true)
            .setUseHardwareAcousticEchoCanceler(true)
            .createAudioDeviceModule()
        return PeerConnectionFactory.builder().apply {
            setAudioDeviceModule(adm)
        }.setOptions(PeerConnectionFactory.Options().apply {
            disableNetworkMonitor = false
            disableEncryption = false
        }).createPeerConnectionFactory()
    }

    /*private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setOptions(PeerConnectionFactory.Options().apply {
                disableNetworkMonitor = false
                disableEncryption = false

            }).createPeerConnectionFactory()
    }*/

    fun initializeWebrtcClient(
        username: String,
        language: String,
        observer: PeerConnection.Observer
    ) {
        magicAudioProcessor.initialize(
            userId = username
        )
        Log.d("$VCT_LOGS initializeWebrtcClient: ", "initializeWebrtcClient $username")
        this.username = username
        this.language = language
        localTrackId = "${username}_track"
        peerConnection = createPeerConnection(observer)
    }

    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        Log.d("$VCT_LOGS createPeerConnection", iceServer.toString())
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    //negotiation section
    fun call(target: String, targetLanguage: String) {
        magicAudioProcessor.setIsMagicNeeded(
            isMagicNeeded = targetLanguage != language,
            targetLanguage = targetLanguage
        )
        peerConnection?.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        val callDataModel = CallDataModel(
                            type = CallDataModelType.Offer,
                            sender = username,
                            target = target,
                            data = desc?.description,
                            language = language
                        )
                        onTransferEventToSocket(
                            callDataModel
                        )
                        Log.d("$VCT_LOGS call", callDataModel.toString())
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun answer(target: String, targetLanguage: String) {
        magicAudioProcessor.setIsMagicNeeded(
            targetLanguage != language,
            targetLanguage
        )
        try {
            peerConnection?.createAnswer(object : MySdpObserver() {
                override fun onCreateSuccess(desc: SessionDescription?) {
                    super.onCreateSuccess(desc)
                    peerConnection?.setLocalDescription(object : MySdpObserver() {
                        override fun onSetSuccess() {
                            super.onSetSuccess()
                            val callDataModel = CallDataModel(
                                type = CallDataModelType.Answer,
                                sender = username,
                                target = target,
                                data = desc?.description,
                                language = language
                            )
                            // TODO : Update Firebase with the Answer
                            onTransferEventToSocket(
                                callDataModel
                            )
                            Log.d("$VCT_LOGS answer", callDataModel.toString())
                        }
                    }, desc)
                }

                override fun onCreateFailure(p0: String?) {
                    Log.d("$VCT_LOGS onCreateFailure: ", p0.toString())
                }

                override fun onSetFailure(p0: String?) {
                    Log.d("$VCT_LOGS onSetFailure: ", p0.toString())
                }
            }, mediaConstraint)

        } catch (e: Exception) {
            Log.d("$VCT_LOGS createAnswer: ", e.toString())
        }
    }

    private fun onTransferEventToSocket(callDataModel: CallDataModel) {
        scope.launch {
            sendConnectionUpdateUseCase.invoke(callDataModel)
        }
    }

    fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    fun addIceCandidateToPeer(iceCandidate: IceCandidate) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun sendIceCandidate(target: String, iceCandidate: IceCandidate) {
        addIceCandidateToPeer(iceCandidate)
        val callDataModel = CallDataModel(
            type = CallDataModelType.IceCandidates,
            sender = username,
            target = target,
            data = Json.encodeToString(IceCandidateSerializer, iceCandidate),
            language = language
        )
        onTransferEventToSocket(
            callDataModel
        )
        Log.d("$VCT_LOGS sendIceCandidate", callDataModel.toString())
    }

    fun closeConnection() {
        try {
            localAudioTrack = null
            rtpSenderTrack = null
            peerConnection?.close()
            magicAudioProcessor.cleanResources()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        try {
            if (shouldBeMuted) {
                rtpSenderTrack?.track()?.setEnabled(false)
            } else {
                rtpSenderTrack?.track()?.setEnabled(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleSpeaker(shouldBeSpeaker: Boolean) {
        // TODO : Implementation needs further investigation...
    }

    fun startLocalStreaming() {
        // TODO : java.lang.IllegalStateException: C++ addTrack failed.
        // Fails when try to add a new tracker after a previous call
        try {
            localAudioTrack =
                peerConnectionFactory.createAudioTrack(localTrackId + "_audio", localAudioSource)
            rtpSenderTrack = peerConnection?.addTrack(localAudioTrack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
        magicAudioProcessor.setScope(
            scope = scope
        )
    }
}