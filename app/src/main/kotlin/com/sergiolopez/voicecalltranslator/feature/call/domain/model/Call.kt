package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class Call : Parcelable {

    @Parcelize
    data object CallNoData : Call(), Parcelable

    @Serializable
    @Parcelize
    data class CallData(
        val callerId: String,
        val calleeId: String,
        val offerData: String,
        val isIncoming: Boolean,
        val callStatus: CallStatus,
        val language: String,
        val timestamp: Long
    ) : Call(), Parcelable
}
