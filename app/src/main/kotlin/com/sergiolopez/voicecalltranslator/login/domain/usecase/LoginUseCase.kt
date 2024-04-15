package com.sergiolopez.voicecalltranslator.login.domain.usecase

import com.sergiolopez.voicecalltranslator.common.data.FirebaseRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return firebaseRepository.login(
            email = email,
            password = password
        )
    }
}