package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.data.repository.MagicAudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class GetLastTranslationUseCase @Inject constructor(
    private val magicAudioRepository: MagicAudioRepository
) {

    operator fun invoke(): Flow<String> {
        return magicAudioRepository.lastTranslation.mapNotNull { it }
    }
}