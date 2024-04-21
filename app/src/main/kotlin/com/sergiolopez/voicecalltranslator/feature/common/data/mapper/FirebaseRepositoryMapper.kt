package com.sergiolopez.voicecalltranslator.feature.common.data.mapper

import com.sergiolopez.voicecalltranslator.feature.call.data.CallDatabase
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserDatabase
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus
import javax.inject.Inject

class FirebaseRepositoryMapper @Inject constructor() {

    fun mapUserDatabaseToUserData(userDatabase: UserDatabase): User.UserData {
        return User.UserData(
            id = userDatabase.id ?: "",
            email = userDatabase.email ?: "",
            creationDate = userDatabase.creationDate ?: "",
            lastLogin = userDatabase.lastLogin ?: "",
            uuid = userDatabase.uuid ?: "",
            status = UserStatus.fromString(userDatabase.status ?: "")
        )
    }

    fun mapUserDataToUserDatabase(user: User.UserData): UserDatabase {
        return UserDatabase(
            id = user.id,
            email = user.email,
            creationDate = user.creationDate,
            lastLogin = user.lastLogin,
            uuid = user.uuid,
            status = user.status.name
        )
    }

    fun mapCallDatabaseToCall(callDatabase: CallDatabase): Call.CallData {
        return Call.CallData(
            callerId = callDatabase.callerId ?: "",
            calleeId = callDatabase.calleeId ?: "",
            offerData = callDatabase.offerId ?: "",
            answerData = callDatabase.answerId ?: "",
            isIncoming = callDatabase.isIncoming ?: false,
            callStatus = callDatabase.callStatus ?: CallStatus.CALL_IN_PROGRESS
        )
    }

    fun mapCallToCallDatabase(call: Call.CallData): CallDatabase {
        return CallDatabase(
            callerId = call.callerId,
            calleeId = call.calleeId,
            offerId = call.offerData,
            answerId = call.answerData,
            isIncoming = call.isIncoming,
            callStatus = call.callStatus
        )
    }
}