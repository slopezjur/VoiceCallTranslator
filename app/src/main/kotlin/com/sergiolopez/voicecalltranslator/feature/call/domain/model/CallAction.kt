package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface CallAction : Parcelable {

    @Parcelize
    data object Answer : CallAction, Parcelable

    @Parcelize
    data class ToggleMute(val isMuted: Boolean) : CallAction, Parcelable

    @Parcelize
    data class ToggleSpeaker(val isSpeaker: Boolean) : CallAction, Parcelable

    @Parcelize
    data object Disconnect : CallAction, Parcelable
}