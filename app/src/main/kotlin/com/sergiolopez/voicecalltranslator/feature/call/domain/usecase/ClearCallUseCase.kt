package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import javax.inject.Inject

class ClearCallUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {
    suspend fun invoke(target: String) {
        return firebaseDatabaseRepository.clearCall(
            target = target
        )
    }
}