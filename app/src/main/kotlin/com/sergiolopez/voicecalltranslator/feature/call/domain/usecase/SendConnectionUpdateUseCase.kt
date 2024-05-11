package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import javax.inject.Inject

class SendConnectionUpdateUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {
    suspend fun invoke(call: DataModel) {
        return firebaseDatabaseRepository.sendConnectionUpdate(
            call = call
        )
    }
}