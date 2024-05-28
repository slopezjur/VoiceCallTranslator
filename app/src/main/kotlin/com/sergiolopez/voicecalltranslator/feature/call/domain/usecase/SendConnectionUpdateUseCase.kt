package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.CallDataModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import javax.inject.Inject

class SendConnectionUpdateUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {
    suspend fun invoke(call: CallDataModel) {
        return firebaseDatabaseRepository.sendConnectionUpdate(
            call = call
        )
    }
}