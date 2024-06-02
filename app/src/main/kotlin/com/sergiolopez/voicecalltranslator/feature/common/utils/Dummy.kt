package com.sergiolopez.voicecalltranslator.feature.common.utils

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.AccountSettingsData
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption

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
        user.copy(email = "john.doe@example.com"),
        user.copy(email = "jane.smith@example.com"),
        user.copy(email = "michael.brown@example.com"),
        user.copy(email = "emily.davis@example.com"),
        user.copy(email = "daniel.jones@example.com"),
        user.copy(email = "laura.martin@example.com"),
        user.copy(email = "robert.garcia@example.com"),
        user.copy(email = "linda.hernandez@example.com"),
        user.copy(email = "william.miller@example.com"),
        user.copy(email = "jessica.lopez@example.com"),
        user.copy(email = "james.wilson@example.com"),
        user.copy(email = "sarah.moore@example.com"),
        user.copy(email = "david.taylor@example.com"),
        user.copy(email = "mary.anderson@example.com"),
        user.copy(email = "richard.thomas@example.com"),
        user.copy(email = "patricia.jackson@example.com"),
        user.copy(email = "charles.white@example.com"),
        user.copy(email = "barbara.lewis@example.com")
    )

    val contact = Contact(
        id = "01",
        email = "slopezjur@uco.edu"
    )

    val accountSettingsDataDark = AccountSettingsData(
        themeOption = ThemeOption.DARK
    )

    val accountSettingsDataLight = AccountSettingsData(
        themeOption = ThemeOption.LIGHT
    )

    val message = Message(
        text = "Hey there, it's me!",
        timestamp = 1716847321565,
        isSent = true
    )

    val messages = listOf(
        message,
        Message(
            text = "I see, I see...",
            timestamp = 1716848321565,
            isSent = false
        ),
        Message(
            text = "This is a very, very, very long long long text! Very very long long very long text!",
            timestamp = 1716857321565,
            isSent = true
        ),
        Message(
            text = "This is an awesome text!",
            timestamp = 1716858321565,
            isSent = true
        ),
        Message(
            text = "I think this is finally working. WDYT?",
            timestamp = 1716859321565,
            isSent = true
        ),
        Message(
            text = "I'm not sure... it is failing very often, you should check the code to be sure",
            timestamp = 1716860321565,
            isSent = false
        ),
        Message(
            text = "Let me try one more time with a new message to verify that everything is OK this time...",
            timestamp = 1716860521565,
            isSent = true
        ),
        Message(
            text = "I will send one more, just in case",
            timestamp = 1716860821565,
            isSent = false
        ),
        Message(
            text = "Ok, it's fine. I'm sure you know what you are doing!",
            timestamp = 1716860991565,
            isSent = true
        )
    )
}