package com.sergiolopez.voicecalltranslator.feature.contactlist.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseDatabaseRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserListUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(userId: String): Result<Flow<List<User>>> {
        return firebaseDatabaseRepository.getUserList(userId)
    }
}