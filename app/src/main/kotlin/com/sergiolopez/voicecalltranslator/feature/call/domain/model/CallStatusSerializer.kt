package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CallStatusSerializer : KSerializer<CallStatus> {
    override fun serialize(encoder: Encoder, value: CallStatus) {
        encoder.encodeString(value.name)
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CallStatus", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CallStatus {
        return CallStatus.valueOf(decoder.decodeString())
    }
}
