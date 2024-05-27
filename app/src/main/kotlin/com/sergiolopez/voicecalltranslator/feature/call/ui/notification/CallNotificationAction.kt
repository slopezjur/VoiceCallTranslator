package com.sergiolopez.voicecalltranslator.feature.call.ui.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface CallNotificationAction : Parcelable {

    @Parcelize
    data object Answer : CallNotificationAction

    @Parcelize
    data object Disconnect : CallNotificationAction
}