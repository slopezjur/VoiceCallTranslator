package com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

enum class DataModelType {
    StartAudioCall, Offer, Answer, IceCandidates, EndCall
}


@Parcelize
@Serializable
data class DataModel(
    val sender: String? = null,
    val target: String,
    val type: DataModelType,
    val data: String? = null,
    val language: String? = null,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable