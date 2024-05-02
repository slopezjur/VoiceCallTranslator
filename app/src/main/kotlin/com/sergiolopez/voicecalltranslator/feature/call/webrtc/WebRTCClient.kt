package com.sergiolopez.voicecalltranslator.feature.call.webrtc

import android.content.Context
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SendConnectionUpdateUseCase
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModel
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRTCClient @Inject constructor(
    private val context: Context,
    private val sendConnectionUpdateUseCase: SendConnectionUpdateUseCase
) {
    //class variables
    private lateinit var username: String

    //webrtc variables
    private val eglBaseContext = EglBase.create().eglBaseContext
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private var peerConnection: PeerConnection? = null
    private val iceServer = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer(),
        PeerConnection.IceServer.builder("stun:stun.relay.metered.ca:80")
            .createIceServer()
    )
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }
    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
    }

    //call variables
    private var localStream: MediaStream? = null
    private var localTrackId = ""
    private var localStreamId = ""
    private var localAudioTrack: AudioTrack? = null

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
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(
                DefaultVideoDecoderFactory(eglBaseContext)
            ).setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBaseContext, true, true
                )
            ).setOptions(PeerConnectionFactory.Options().apply {
                disableNetworkMonitor = false
                disableEncryption = false
            }).createPeerConnectionFactory()
    }

    fun initializeWebrtcClient(
        username: String, observer: PeerConnection.Observer
    ) {
        this.username = username
        localTrackId = "${username}_track"
        localStreamId = "${username}_stream"
        peerConnection = createPeerConnection(observer)
    }

    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        Log.d("VCT_LOGS createPeerConnection", iceServer.toString())
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    //negotiation section
    fun call(target: String) {
        peerConnection?.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        val dataModel = DataModel(
                            type = DataModelType.Offer,
                            sender = username,
                            target = target,
                            data = desc?.description
                        )
                        onTransferEventToSocket(
                            dataModel
                        )
                        Log.d("VCT_LOGS call", dataModel.toString())

                        /*onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.OFFER,
                                dataModel.data.toString()
                            )
                        )*/
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun answer(target: String) {
        try {
            peerConnection?.createAnswer(object : MySdpObserver() {
                override fun onCreateSuccess(desc: SessionDescription?) {
                    super.onCreateSuccess(desc)
                    peerConnection?.setLocalDescription(object : MySdpObserver() {
                        override fun onSetSuccess() {
                            super.onSetSuccess()
                            val dataModel = DataModel(
                                type = DataModelType.Answer,
                                sender = username,
                                target = target,
                                data = desc?.description
                            )
                            /*onRemoteSessionReceived(
                                SessionDescription(
                                    SessionDescription.Type.ANSWER,
                                    dataModel.data.toString()
                                )
                            )*/
                            // TODO : Update Firebase with the Answer
                            onTransferEventToSocket(
                                dataModel
                            )
                            Log.d("VCT_LOGS answer", dataModel.toString())
                        }
                    }, desc)
                }

                override fun onCreateFailure(p0: String?) {
                    Log.d("Such a BIG FAIL: ", p0.toString())
                }

                override fun onSetFailure(p0: String?) {
                    Log.d("Such a BIG FAIL: ", p0.toString())
                }
            }, mediaConstraint)

        } catch (e: Exception) {
            Log.d("Such a BIG FAIL: ", e.toString())
        }
    }

    private fun onTransferEventToSocket(dataModel: DataModel) {
        scope.launch {
            sendConnectionUpdateUseCase.invoke(dataModel)
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
        val dataModel = DataModel(
            type = DataModelType.IceCandidates,
            sender = username,
            target = target,
            data = Json.encodeToString(IceCandidateSerializer, iceCandidate)
        )
        onTransferEventToSocket(
            dataModel
        )
        Log.d("VCT_LOGS sendIceCandidate", dataModel.toString())
    }

    fun closeConnection() {
        try {
            localStream?.dispose()
            peerConnection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        if (shouldBeMuted) {
            localStream?.removeTrack(localAudioTrack)
        } else {
            localStream?.addTrack(localAudioTrack)
        }
    }

    fun initLocalSurfaceView() {
        //this.localSurfaceView = localView
        //initSurfaceView(localView)
        startLocalStreaming()
    }

    private fun startLocalStreaming() {
        localStream = peerConnectionFactory.createLocalMediaStream(localStreamId)
        localAudioTrack =
            peerConnectionFactory.createAudioTrack(localTrackId + "_audio", localAudioSource)
        localStream?.addTrack(localAudioTrack)
        peerConnection?.addStream(localStream)
    }

    private fun getVideoCapturer(context: Context): CameraVideoCapturer =
        Camera2Enumerator(context).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException()
        }

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }
}