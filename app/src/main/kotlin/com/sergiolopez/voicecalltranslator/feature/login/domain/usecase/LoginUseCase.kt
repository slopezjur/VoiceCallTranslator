package com.sergiolopez.voicecalltranslator.feature.login.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return firebaseAuthRepository.login(
            email = email,
            password = password
        )
    }
}