package com.sergiolopez.voicecalltranslator.feature.common.data.mapper

import com.sergiolopez.voicecalltranslator.feature.call.data.CallData
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserData
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus
import javax.inject.Inject

class FirebaseRepositoryMapper @Inject constructor() {

    fun mapUserDataToUser(userData: UserData): User {
        return User(
            id = userData.id ?: "",
            email = userData.email ?: "",
            creationDate = userData.creationDate ?: "",
            lastLogin = userData.lastLogin ?: "",
            uuid = userData.uuid ?: "",
            status = UserStatus.fromString(userData.status ?: "")
        )
    }

    fun mapUserToUserData(user: User): UserData {
        return UserData(
            id = user.id,
            email = user.email,
            creationDate = user.creationDate,
            lastLogin = user.lastLogin,
            uuid = user.uuid,
            status = user.status.name
        )
    }

    fun mapCallDataToCall(callData: CallData): Call {
        return Call(
            callerId = callData.callerId ?: "",
            calleeId = callData.calleeId ?: "",
            offerData = callData.offerId ?: "",
            answerData = callData.answerId ?: "",
            isIncoming = callData.isIncoming ?: false,
            callStatus = callData.callStatus ?: CallStatus.CALL_IN_PROGRESS
        )
    }

    fun mapCallToCallData(call: Call): CallData {
        return CallData(
            callerId = call.callerId,
            calleeId = call.calleeId,
            offerId = call.offerData,
            answerId = call.answerData,
            isIncoming = call.isIncoming,
            callStatus = call.callStatus
        )
    }
}