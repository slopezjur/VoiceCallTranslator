package com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model


sealed class User {

    data object UserNoData : User()

    data class UserData(
        val id: String,
        val email: String,
        val creationDate: String,
        val lastLogin: String,
        val uuid: String,
        val status: UserStatus
    ) : User()
}