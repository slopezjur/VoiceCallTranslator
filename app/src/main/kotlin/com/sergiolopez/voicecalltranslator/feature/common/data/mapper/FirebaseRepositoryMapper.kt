package com.sergiolopez.voicecalltranslator.feature.common.data.mapper

import com.sergiolopez.voicecalltranslator.feature.call.data.CallDatabase
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserDatabase
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus
import javax.inject.Inject

class FirebaseRepositoryMapper @Inject constructor() {

    fun mapUserDatabaseToUserData(userDatabase: UserDatabase): User {
        return User(
            id = userDatabase.id ?: "",
            email = userDatabase.email ?: "",
            creationDate = userDatabase.creationDate ?: "",
            lastLogin = userDatabase.lastLogin ?: "",
            uuid = userDatabase.uuid ?: "",
            status = UserStatus.fromString(userDatabase.status ?: "")
        )
    }

    fun mapUserDataToUserDatabase(user: User): UserDatabase {
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
            callerEmail = callDatabase.callerEmail ?: "",
            calleeId = callDatabase.calleeId ?: "",
            calleeEmail = callDatabase.calleeEmail ?: "",
            offerData = callDatabase.offerData ?: "",
            isIncoming = callDatabase.isIncoming ?: false,
            callStatus = callDatabase.callStatus ?: CallStatus.CALL_IN_PROGRESS,
            language = callDatabase.language,
            timestamp = callDatabase.timestamp ?: 0
        )
    }

    fun mapCallToCallDatabase(call: Call.CallData): CallDatabase {
        return CallDatabase(
            callerId = call.callerId,
            callerEmail = call.callerEmail,
            calleeId = call.calleeId,
            calleeEmail = call.calleeEmail,
            offerData = call.offerData,
            isIncoming = call.isIncoming,
            language = call.language,
            callStatus = call.callStatus
        )
    }
}