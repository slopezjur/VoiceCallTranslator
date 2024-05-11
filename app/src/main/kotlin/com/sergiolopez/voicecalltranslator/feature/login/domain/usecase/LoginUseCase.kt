package com.sergiolopez.voicecalltranslator.feature.login.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {

    suspend operator fun invoke(email: String, password: String): Boolean {
        return firebaseAuthRepository.login(
            email = email,
            password = password
        )
    }
}