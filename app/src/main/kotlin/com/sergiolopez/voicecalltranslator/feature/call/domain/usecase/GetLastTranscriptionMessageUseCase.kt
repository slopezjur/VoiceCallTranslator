package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.data.repository.MagicAudioRepository
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.sql.Timestamp
import java.time.Instant
import javax.inject.Inject

class GetLastTranscriptionMessageUseCase @Inject constructor(
    private val magicAudioRepository: MagicAudioRepository
) {

    operator fun invoke(): Flow<Message> {
        return magicAudioRepository.lastTranscription.mapNotNull {
            it?.let {
                Message(
                    text = it,
                    isSent = true,
                    timestamp = Timestamp.from(Instant.now()).time
                )
            }
        }
    }
}