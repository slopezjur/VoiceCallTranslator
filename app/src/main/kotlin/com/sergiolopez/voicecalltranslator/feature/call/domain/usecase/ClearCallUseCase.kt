package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import javax.inject.Inject

class ClearCallUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {
    suspend fun invoke(userId: String) {
        return firebaseDatabaseRepository.clearCall(
            userId = userId
        )
    }
}