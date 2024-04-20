package com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    suspend operator fun invoke(user: User) {
        return firebaseDatabaseRepository.saveUser(user)
    }
}