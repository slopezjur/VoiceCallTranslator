package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Call {

    data object CallNoData : Call()

    @Parcelize
    data class CallData(
        val callerId: String,
        val calleeId: String,
        val offerData: String?,
        val answerData: String?,
        val isIncoming: Boolean,
        val callStatus: CallStatus
    ) : Parcelable, Call()
}
