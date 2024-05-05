package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class Call {

    data object CallNoData : Call()

    @Parcelize
    @Serializable
    data class CallData(
        val callerId: String,
        val calleeId: String,
        val offerData: String,
        val answerData: String,
        val isIncoming: Boolean,
        val callStatus: CallStatus,
        val timestamp: Long
    ) : Parcelable, Call()
}
