package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectionUpdateUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(userId: String): Result<Flow<DataModel>> {
        return firebaseDatabaseRepository.getConnectionUpdate(
            userId = userId
        )
    }
}