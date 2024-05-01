package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import javax.inject.Inject

class AnswerCallUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {
    suspend fun invoke(call: Call.CallData) {
        return firebaseDatabaseRepository.answerCall(
            call = call
        )
    }
}