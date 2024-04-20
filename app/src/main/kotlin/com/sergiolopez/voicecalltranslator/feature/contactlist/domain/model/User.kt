package com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model

data class User(
    val id: String,
    val email: String,
    val creationDate: String,
    val lastLogin: String,
    val uuid: String
)