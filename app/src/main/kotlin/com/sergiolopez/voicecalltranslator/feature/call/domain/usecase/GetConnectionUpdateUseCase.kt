package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.CallDataModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectionUpdateUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(userId: String): Result<Flow<CallDataModel>> {
        return firebaseDatabaseRepository.getConnectionUpdate(
            userId = userId
        )
    }
}