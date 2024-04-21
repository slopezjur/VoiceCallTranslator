package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.feature.call.telecom.repository.TelecomCallRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetTelecomCallUseCase @Inject constructor(
    private val telecomCallRepository: TelecomCallRepository
) {

    fun invoke(): StateFlow<TelecomCall> {
        return telecomCallRepository.currentCall
    }
}