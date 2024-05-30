package com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

enum class CallDataModelType {
    StartAudioCall, Offer, Answer, IceCandidates, Message, EndCall
}

@Parcelize
@Serializable
data class CallDataModel(
    val sender: String? = null,
    val target: String,
    val data: String? = null,
    val type: CallDataModelType,
    val language: String,
    val message: String? = null,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable