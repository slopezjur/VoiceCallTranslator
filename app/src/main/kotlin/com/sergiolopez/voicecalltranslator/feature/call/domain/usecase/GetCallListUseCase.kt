package com.sergiolopez.voicecalltranslator.feature.call.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCallListUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(): Result<Flow<List<Call>>> {
        return firebaseDatabaseRepository.getCallList()
    }
}