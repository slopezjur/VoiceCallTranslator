package com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model

import java.util.Locale

enum class UserStatus {
    OFFLINE,
    ONLINE,
    BUSY;

    companion object {
        fun fromString(value: String): UserStatus {
            return when (value.uppercase(Locale.ROOT)) {
                OFFLINE.name -> OFFLINE
                ONLINE.name -> ONLINE
                BUSY.name -> BUSY
                else -> OFFLINE
            }
        }
    }
}
