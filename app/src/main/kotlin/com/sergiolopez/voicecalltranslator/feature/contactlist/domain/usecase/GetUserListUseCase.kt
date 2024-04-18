package com.sergiolopez.voicecalltranslator.feature.contactlist.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserListUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    operator fun invoke(): Result<Flow<List<User>>> {
        return  firebaseDatabaseRepository.getUserList()
    }
}