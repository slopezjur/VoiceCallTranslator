package com.sergiolopez.voicecalltranslator.feature.common.utils

import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User

object Dummy {

    val user = User(
        id = "slopezjur@uco.edu",
        creationDate = "15 abr 2024",
        lastLogin = "15 abr 2024",
        uuid = "uuid example"
    )

    val userList = listOf(
        user,
        user.copy(id = "test@test.com"),
        user.copy(id = "test2@test.com")
    )
}