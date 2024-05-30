package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.Timestamp
import java.time.Instant
import javax.inject.Inject

class GetLastMessageFromCallUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(userId: String): Result<Flow<Message>> {
        return firebaseDatabaseRepository.getLastMessage(
            userId = userId
        ).map {
            it.map { message ->
                Message(
                    text = message,
                    isSent = false,
                    timestamp = Timestamp.from(Instant.now()).time
                )
            }
        }
    }
}