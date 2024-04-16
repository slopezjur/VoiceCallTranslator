package com.sergiolopez.voicecalltranslator.feature.common.data.mapper

import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserData
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun mapUserDataToUser(userData: UserData): User {
        return User(
            id = userData.id,
            creationDate = userData.creationDate,
            lastLogin = userData.lastLogin,
            uuid = userData.uuid
        )
    }

    fun mapUserToUserData(user: User): UserData {
        return UserData(
            id = user.id,
            creationDate = user.creationDate,
            lastLogin = user.lastLogin,
            uuid = user.uuid
        )
    }
}