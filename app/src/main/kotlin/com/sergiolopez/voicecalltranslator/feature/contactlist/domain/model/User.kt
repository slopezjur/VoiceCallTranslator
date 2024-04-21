package com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model


sealed class User {

    data object None : User()

    data class Logged(
        val id: String,
        val email: String,
        val creationDate: String,
        val lastLogin: String,
        val uuid: String,
        val status: UserStatus
    ) : User()

    data class NotLogged(
        val email: String
    ) : User()
}