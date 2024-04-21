package com.sergiolopez.voicecalltranslator.feature.call.webrtc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.webrtc.IceCandidate

object IceCandidateSerializer : KSerializer<IceCandidate> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IceCandidate") {
        element<String>("sdpMid")
        element<Int>("sdpMLineIndex")
        element<String>("sdp")
        element<String>("serverUrl")
        element<String>("adapterType")
    }

    override fun serialize(encoder: Encoder, value: IceCandidate) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeStringElement(descriptor, 0, value.sdpMid)
        compositeOutput.encodeIntElement(descriptor, 1, value.sdpMLineIndex)
        compositeOutput.encodeStringElement(descriptor, 2, value.sdp)
        compositeOutput.encodeStringElement(descriptor, 3, value.serverUrl)
        compositeOutput.encodeStringElement(descriptor, 4, value.adapterType.name)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): IceCandidate {
        val dec = decoder.beginStructure(descriptor)
        var sdpMid: String? = null
        var sdpMLineIndex: Int? = null
        var sdp: String? = null
        var serverUrl: String? = null
        var adapterType: String? = null
        loop@ while (true) {
            when (val index = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> sdpMid = dec.decodeStringElement(descriptor, 0)
                1 -> sdpMLineIndex = dec.decodeIntElement(descriptor, 1)
                2 -> sdp = dec.decodeStringElement(descriptor, 2)
                3 -> serverUrl = dec.decodeStringElement(descriptor, 3)
                4 -> adapterType = dec.decodeStringElement(descriptor, 4)
                else -> throw SerializationException("Unexpected index $index")
            }
        }
        dec.endStructure(descriptor)
        if (sdpMid == null || sdpMLineIndex == null || sdp == null || serverUrl == null || adapterType == null) throw SerializationException(
            "Missing value"
        )
        //return IceCandidate(sdpMid, sdpMLineIndex, sdp, serverUrl, PeerConnection.AdapterType.valueOf(adapterType))
        return IceCandidate(sdpMid, sdpMLineIndex, sdp)
    }
}
