package com.sergiolopez.voicecalltranslator

import org.webrtc.PeerConnection

object VctApiKeys {
    const val OPEN_AI_API_KEY = "WRITE HERE YOUR API KEY"
    val ICE_SERVERS = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer(),
        PeerConnection.IceServer.builder("stun:stun.relay.metered.ca:80")
            .createIceServer(),
        /*PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80")
            .setUsername("user")
            .setPassword("password").createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80?transport=tcp")
            .setUsername("user")
            .setPassword("password").createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:443")
            .setUsername("user")
            .setPassword("password").createIceServer(),
        PeerConnection.IceServer.builder("turns:global.relay.metered.ca:443?transport=tcp")
            .setUsername("user")
            .setPassword("password").createIceServer()*/
    )
}