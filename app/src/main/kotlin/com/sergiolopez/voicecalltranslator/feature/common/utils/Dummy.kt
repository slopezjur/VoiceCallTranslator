package com.sergiolopez.voicecalltranslator.feature.common.utils

import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus

object Dummy {

    val user = User(
        id = "01",
        email = "slopezjur@uco.edu",
        creationDate = "15 abr 2024",
        lastLogin = "15 abr 2024",
        uuid = "uuid example",
        status = UserStatus.ONLINE
    )

    val userList = listOf(
        user,
        user.copy(email = "test@test.com"),
        user.copy(email = "test2@test.com")
    )
}