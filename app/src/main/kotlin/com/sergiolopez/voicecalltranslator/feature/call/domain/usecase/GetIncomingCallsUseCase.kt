package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIncomingCallsUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(calleeId: String): Result<Flow<List<Call.CallData>>> {
        return firebaseDatabaseRepository.getIncomingCalls(
            calleeId = calleeId
        )
    }
}